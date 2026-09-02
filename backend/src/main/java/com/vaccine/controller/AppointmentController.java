package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Admin;
import com.vaccine.entity.Appointment;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 预约控制器
 */
@Tag(name = "预约管理")
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AdminMapper adminMapper;

    @Operation(summary = "获取预约详情")
    @GetMapping("/{id}")
    @RequireRole({"admin", "community_admin", "user"})
    public Result<Appointment> getById(@PathVariable Long id) {
        return Result.success(appointmentService.findAccessibleById(id));
    }

    @Operation(summary = "分页查询预约")
    @GetMapping("/page")
    @RequireRole({"admin", "community_admin", "user"})
    public Result<PageResult<Appointment>> page(Appointment appointment,
                                                @RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        // 社区管理员只能查看本社区的预约
        if (UserContext.isCommunityAdmin()) {
            Admin admin = adminMapper.findById(UserContext.getUserId());
            appointment.setCommunityId(admin.getCommunityId());
        } else if (UserContext.isAdmin()) {
            // 系统管理员只能查看已处理的预约（排除待审核状态），不能介入业务审核
            appointment.setExcludePendingStatus(true);
        } else {
            // 普通用户只能查看自己的记录，不能通过查询参数越权读取其他人的预约。
            appointment.setUserId(UserContext.getUserId());
        }
        return Result.success(appointmentService.findPage(appointment, pageNum, pageSize));
    }

    @Operation(summary = "我的预约列表")
    @GetMapping("/my")
    @RequireRole("user")
    public Result<List<Appointment>> myAppointments() {
        Long userId = UserContext.getUserId();
        return Result.success(appointmentService.findByUserId(userId));
    }

    @Operation(summary = "创建预约")
    @PostMapping
    @RequireRole("user")
    public Result<?> create(@Valid @RequestBody Appointment appointment) {
        appointmentService.createAppointment(appointment);
        return Result.success("预约成功，请等待审核");
    }

    @Operation(summary = "审核预约")
    @PostMapping("/audit")
    @RequireRole("community_admin")
    public Result<?> audit(@Valid @RequestBody AuditDTO dto) {
        appointmentService.audit(dto.getId(), dto.getStatus(), dto.getRejectReason());
        return Result.success("审核完成");
    }

    @Operation(summary = "支付预约")
    @PostMapping("/{id}/pay")
    @RequireRole("user")
    public Result<?> pay(@PathVariable Long id) {
        appointmentService.pay(id);
        return Result.success("支付成功");
    }

    @Operation(summary = "确认接种完成")
    @PostMapping("/{id}/vaccinate")
    @RequireRole("community_admin")
    public Result<?> vaccinate(@PathVariable Long id) {
        appointmentService.confirmVaccinate(id);
        return Result.success("确认接种完成");
    }

    @Operation(summary = "取消预约")
    @PostMapping("/{id}/cancel")
    @RequireRole("user")
    public Result<?> cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return Result.success("取消成功");
    }

    @Operation(summary = "获取时段剩余容量")
    @GetMapping("/time-slot/remaining")
    @RequireRole("user")
    public Result<Integer> getTimeSlotRemaining(
            @RequestParam Long communityId,
            @RequestParam String date,
            @RequestParam String timeSlot) {
        int remaining = appointmentService.getTimeSlotRemaining(communityId, date, timeSlot);
        return Result.success(remaining);
    }

    @Data
    public static class AuditDTO {
        @NotNull(message = "预约记录不能为空")
        private Long id;

        @NotNull(message = "审核状态不能为空")
        @Min(value = 1, message = "审核状态不合法")
        @Max(value = 2, message = "审核状态不合法")
        private Integer status; // 1-通过，2-拒绝

        @Size(max = 500, message = "拒绝原因长度不能超过 500 个字符")
        private String rejectReason;
    }
}
