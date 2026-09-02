package com.vaccine.service;

import cn.hutool.core.util.IdUtil;
import com.vaccine.common.PageResult;
import com.vaccine.common.UserContext;
import com.vaccine.entity.Admin;
import com.vaccine.entity.Appointment;
import com.vaccine.entity.TimeSlotCapacity;
import com.vaccine.entity.User;
import com.vaccine.entity.Vaccine;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AppointmentMapper;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.mapper.TimeSlotCapacityMapper;
import com.vaccine.mapper.UserMapper;
import com.vaccine.mapper.VaccineMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 预约服务
 * 实现事务控制、容量控制、通知功能
 * 集成分布式锁和限流
 */
@Slf4j
@Service
public class AppointmentService {

    // 每个时段默认最大预约容量
    private static final int DEFAULT_TIME_SLOT_CAPACITY = 50;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private VaccineMapper vaccineMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DistributedLockService lockService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TimeSlotCapacityMapper timeSlotCapacityMapper;

    public Appointment findById(Long id) {
        return appointmentMapper.findById(id);
    }

    /**
     * 创建预约（事务保证原子性 + 分布式锁）
     * 包含：限流检查、库存检查、时段容量控制、库存扣减
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAppointment(Appointment appointment) {
        validateCreateRequest(appointment);
        Long userId = UserContext.getUserId(); // 获取当前登录的用户ID
        if (userId == null || !UserContext.isUser()) {
            throw BusinessException.of(403, "仅普通用户可以创建预约");
        }
        Long vaccineId = appointment.getVaccineId();// 获取疫苗ID
        //拿到用户当前登录的用户ID和疫苗ID，后面的校验和记录创建都依赖他们

        // 1. 预约限流检查 ，防止恶意提交
        if (!rateLimitService.isAppointmentAllowed(userId)) {
            throw BusinessException.of("预约操作过于频繁，请稍后重试");
        }

        // 2. 获取预约操作锁（防止重复预约） 用户预约锁
        if (!lockService.lockAppointment(userId, vaccineId)) {
            throw BusinessException.of("正在处理中，请勿重复提交");
        }

        try {
            // 3. 获取疫苗库存锁 减少并发条件下多个请求同时修改同一疫苗都带来的问题
            if (!lockService.lockVaccineStock(vaccineId)) {
                throw BusinessException.of("系统繁忙，请稍后重试");
            }

            try {
                // 4. 检查疫苗状态和库存
                Vaccine vaccine = vaccineMapper.findById(vaccineId);
                if (vaccine == null || vaccine.getStatus() != 1) {
                    throw BusinessException.of("疫苗不存在或已下架");
                }
                if (vaccine.getStock() <= 0) {
                    throw BusinessException.of("疫苗库存不足");
                }

                // 5. 检查并更新时段容量（使用time_slot_capacity表）
                TimeSlotCapacity slotCapacity = getOrCreateTimeSlotCapacity(
                    appointment.getCommunityId(),
                    appointment.getAppointmentDate().toString(),
                    appointment.getTimeSlot()
                );
                if (slotCapacity.getBooked() >= slotCapacity.getCapacity()) {
                    throw BusinessException.of("该时段预约已满，请选择其他时段");
                }

                // 增加已预约数
                int slotRows = timeSlotCapacityMapper.increaseBooked(slotCapacity.getId());
                if (slotRows == 0) {
                    throw BusinessException.of("该时段预约已满，请选择其他时段");
                }

                // 6. 使用乐观锁减少库存（保证原子性）
                int rows = vaccineMapper.decreaseStock(vaccine.getId());
                if (rows == 0) {
                    throw BusinessException.of("疫苗库存不足，请稍后重试");
                }

                // 7. 增加疫苗预约计数
                vaccineMapper.increaseAppointmentCount(vaccine.getId());

                // 8. 创建预约记录
                appointment.setOrderNo(IdUtil.getSnowflakeNextIdStr());
                appointment.setUserId(userId);
                appointment.setAmount(vaccine.getPrice());
                appointment.setStatus(0); // 待审核
                appointmentMapper.insert(appointment);

                // 9. 清除疫苗缓存
                cacheService.clearVaccineCache(vaccineId);

                log.info("用户{}创建预约成功，订单号：{}，疫苗：{}，时段：{} {}", 
                    userId, appointment.getOrderNo(), vaccine.getName(), 
                    appointment.getAppointmentDate(), appointment.getTimeSlot());

            } finally {
                lockService.unlockVaccineStock(vaccineId);
            }
        } finally {
            lockService.unlockAppointment(userId, vaccineId);
        }
    }

    /**
     * 审核预约（事务保证原子性）
     * 审核通过或拒绝后发送通知
     */
    //进入业务层后，系统首先查询预约记录，并校验当前状态是否还是待审核，防止重复处理。
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer status, String rejectReason) {
        Appointment appointment = appointmentMapper.findById(id);
        if (appointment == null) {
            throw BusinessException.of("预约记录不存在");
        }
        ensureCommunityAdminOwnsAppointment(appointment);
        if (appointment.getStatus() != 0) {
            throw BusinessException.of("该预约已处理");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw BusinessException.of(400, "审核状态不合法");
        }
        if (status == 2 && (rejectReason == null || rejectReason.isBlank())) {
            throw BusinessException.of(400, "拒绝预约时请填写原因");
        }
//如果审核拒绝，则将预约状态改为拒绝，并恢复库存、预约计数、时段已预约数。
        appointment.setStatus(status);
        if (status == 2) { // 拒绝
            appointment.setRejectReason(rejectReason);
            restoreReservation(appointment);
        }
        appointmentMapper.update(appointment);

        // 最后会发送通知（短信/邮件）
        try {
            if (status == 1) {
                notificationService.sendApprovalNotification(appointment);
                log.info("预约{}审核通过，已发送通知", appointment.getOrderNo());
            } else if (status == 2) {
                notificationService.sendRejectionNotification(appointment, rejectReason);
                log.info("预约{}审核拒绝，已发送通知", appointment.getOrderNo());
            }
        } catch (Exception e) {
            log.error("发送通知失败", e);
            // 通知失败不影响主流程
        }
    }

    /**
     * 支付预约（事务保证原子性 + 分布式锁）
     * 包含：余额检查、余额扣减、状态更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long id) {
        Long userId = UserContext.getUserId();
        Appointment appointment = appointmentMapper.findById(id);
        
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            throw BusinessException.of("预约记录不存在");
        }
        if (appointment.getStatus() != 1) {
            throw BusinessException.of("该预约状态不允许支付");
        }

        // 获取用户余额锁
        if (!lockService.lockUserBalance(userId)) {
            throw BusinessException.of("系统繁忙，请稍后重试");
        }

        try {
            User user = userMapper.findById(userId);
            if (user.getBalance().compareTo(appointment.getAmount()) < 0) {
                throw BusinessException.of("余额不足，请先充值");
            }

            // 扣除余额（使用乐观锁保证原子性） 如果余额充足，系统会计算支付后的新余额，
            // 并调用 UserMapper 执行带条件的余额扣减更新，防止多个并发请求同时修改同一个账户余额。”
            BigDecimal newBalance = user.getBalance().subtract(appointment.getAmount());
            int rows = userMapper.updateBalanceWithCheck(userId, user.getBalance(), newBalance);
            if (rows == 0) {
                throw BusinessException.of("支付失败，请重试");
            }

            // 更新预约状态
            appointment.setStatus(3); // 已支付
            appointment.setPayTime(LocalDateTime.now());
            appointmentMapper.update(appointment);

            // 发送支付成功通知
            try {
                notificationService.sendPaymentSuccessNotification(appointment);
            } catch (Exception e) {
                log.error("发送支付通知失败", e);
            }

            log.info("用户{}支付预约{}成功，金额：{}", userId, appointment.getOrderNo(), appointment.getAmount());

        } finally {
            lockService.unlockUserBalance(userId);
        }
    }

    /**
     * 确认接种完成
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmVaccinate(Long id) {
        Appointment appointment = appointmentMapper.findById(id);
        if (appointment == null) {
            throw BusinessException.of("预约记录不存在");
        }
        ensureCommunityAdminOwnsAppointment(appointment);
        if (appointment.getStatus() != 3) {
            throw BusinessException.of("该预约状态不允许确认接种");
        }

        appointment.setStatus(4); // 已接种
        appointment.setVaccinateTime(LocalDateTime.now());
        appointmentMapper.update(appointment);

        // 发送接种完成通知
        try {
            notificationService.sendVaccinationCompleteNotification(appointment);
        } catch (Exception e) {
            log.error("发送接种完成通知失败", e);
        }

        log.info("预约{}已确认接种完成", appointment.getOrderNo());
    }

    /**
     * 取消预约（事务保证原子性）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Long userId = UserContext.getUserId();
        Appointment appointment = appointmentMapper.findById(id);
        
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            throw BusinessException.of("预约记录不存在");
        }
        if (appointment.getStatus() == 2) {
            throw BusinessException.of("该预约已被拒绝，库存已自动恢复");
        }
        if (appointment.getStatus() != 0 && appointment.getStatus() != 1 && appointment.getStatus() != 3) {
            throw BusinessException.of("该预约状态不允许取消");
        }

        boolean paidAppointment = appointment.getStatus() == 3;
        if (paidAppointment) {
            refundToBalance(userId, appointment.getAmount());
        }

        restoreReservation(appointment);

        appointment.setStatus(5); // 已取消
        appointmentMapper.update(appointment);

        log.info("用户{}取消预约{}{}", userId, appointment.getOrderNo(),
                paidAppointment ? "，已退回账户余额" : "");
    }

    /**
     * 返回当前角色有权查看的预约详情。
     */
    public Appointment findAccessibleById(Long id) {
        Appointment appointment = appointmentMapper.findById(id);
        if (appointment == null) {
            throw BusinessException.of(404, "预约记录不存在");
        }
        if (UserContext.isUser() && !Objects.equals(appointment.getUserId(), UserContext.getUserId())) {
            throw BusinessException.of(403, "无权查看该预约记录");
        }
        if (UserContext.isCommunityAdmin()) {
            ensureCommunityAdminOwnsAppointment(appointment);
        }
        if (!UserContext.hasRole("admin", "community_admin", "user")) {
            throw BusinessException.of(403, "无权查看该预约记录");
        }
        return appointment;
    }

    private void validateCreateRequest(Appointment appointment) {
        if (appointment == null || appointment.getVaccineId() == null || appointment.getCommunityId() == null
                || appointment.getAppointmentDate() == null || appointment.getTimeSlot() == null
                || appointment.getTimeSlot().isBlank() || appointment.getDoseNumber() == null) {
            throw BusinessException.of(400, "预约信息不完整");
        }
        if (appointment.getVaccineId() <= 0 || appointment.getCommunityId() <= 0 || appointment.getDoseNumber() <= 0) {
            throw BusinessException.of(400, "预约信息不合法");
        }
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw BusinessException.of(400, "预约日期不能早于今天");
        }
    }

    private void ensureCommunityAdminOwnsAppointment(Appointment appointment) {
        if (!UserContext.isCommunityAdmin()) {
            throw BusinessException.of(403, "仅社区管理员可以处理预约");
        }
        Admin admin = adminMapper.findById(UserContext.getUserId());
        if (admin == null || admin.getCommunityId() == null
                || !Objects.equals(admin.getCommunityId(), appointment.getCommunityId())) {
            throw BusinessException.of(403, "无权操作其他社区的预约");
        }
    }

    private void restoreReservation(Appointment appointment) {
        vaccineMapper.increaseStock(appointment.getVaccineId());
        vaccineMapper.decreaseAppointmentCount(appointment.getVaccineId());
        TimeSlotCapacity slotCapacity = timeSlotCapacityMapper.findBySlot(
                appointment.getCommunityId(),
                appointment.getAppointmentDate().toString(),
                appointment.getTimeSlot()
        );
        if (slotCapacity != null) {
            timeSlotCapacityMapper.decreaseBooked(slotCapacity.getId());
        }
        cacheService.clearVaccineCache(appointment.getVaccineId());
    }

    private void refundToBalance(Long userId, BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw BusinessException.of("订单金额异常，无法退款");
        }
        if (!lockService.lockUserBalance(userId)) {
            throw BusinessException.of("系统繁忙，请稍后重试退款");
        }
        try {
            User user = userMapper.findById(userId);
            if (user == null || user.getBalance() == null) {
                throw BusinessException.of("用户账户不存在");
            }
            BigDecimal newBalance = user.getBalance().add(amount);
            if (userMapper.updateBalanceWithCheck(userId, user.getBalance(), newBalance) == 0) {
                throw BusinessException.of("退款失败，请重试");
            }
        } finally {
            lockService.unlockUserBalance(userId);
        }
    }

    /**
     * 获取时段剩余容量（使用time_slot_capacity表）
     */
    public int getTimeSlotRemaining(Long communityId, String date, String timeSlot) {
        TimeSlotCapacity slotCapacity = timeSlotCapacityMapper.findBySlot(communityId, date, timeSlot);
        if (slotCapacity == null) {
            // 如果没有配置，返回默认容量
            return DEFAULT_TIME_SLOT_CAPACITY;
        }
        return Math.max(0, slotCapacity.getCapacity() - slotCapacity.getBooked());
    }

    /**
     * 获取或创建时段容量记录
     */
    private TimeSlotCapacity getOrCreateTimeSlotCapacity(Long communityId, String date, String timeSlot) {
        TimeSlotCapacity slotCapacity = timeSlotCapacityMapper.findBySlot(communityId, date, timeSlot);
        if (slotCapacity == null) {
            slotCapacity = new TimeSlotCapacity();
            slotCapacity.setCommunityId(communityId);
            slotCapacity.setSlotDate(java.time.LocalDate.parse(date));
            slotCapacity.setTimeSlot(timeSlot);
            slotCapacity.setCapacity(DEFAULT_TIME_SLOT_CAPACITY);
            slotCapacity.setBooked(0);
            timeSlotCapacityMapper.insert(slotCapacity);
        }
        return slotCapacity;
    }

    public PageResult<Appointment> findPage(Appointment appointment, int pageNum, int pageSize) {
        Long total = appointmentMapper.count(appointment);
        List<Appointment> list = appointmentMapper.findList(appointment);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<Appointment> findByUserId(Long userId) {
        return appointmentMapper.findByUserId(userId);
    }

    public List<Appointment> findByCommunityId(Long communityId) {
        return appointmentMapper.findByCommunityId(communityId);
    }

    // 统计方法
    public Long countByStatus(Integer status) {
        return appointmentMapper.countByStatus(status);
    }

    public Long countByCommunityIdAndStatus(Long communityId, Integer status) {
        return appointmentMapper.countByCommunityIdAndStatus(communityId, status);
    }

    public List<Map<String, Object>> countByDate(String startDate) {
        return appointmentMapper.countByDate(startDate);
    }

    public List<Map<String, Object>> countByCommunity() {
        return appointmentMapper.countByCommunity();
    }

    public List<Map<String, Object>> countByVaccine() {
        return appointmentMapper.countByVaccine();
    }

    public java.math.BigDecimal sumIncome() {
        return appointmentMapper.sumIncome();
    }

    public List<Map<String, Object>> sumIncomeByDate(String startDate) {
        return appointmentMapper.sumIncomeByDate(startDate);
    }
}
