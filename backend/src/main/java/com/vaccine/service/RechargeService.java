package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.common.UserContext;
import com.vaccine.entity.RechargeRecord;
import com.vaccine.entity.User;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.RechargeRecordMapper;
import com.vaccine.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值服务
 */
@Service
public class RechargeService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DistributedLockService lockService;

    /**
     * 用户充值（事务保证原子性）
     */
    @Transactional(rollbackFor = Exception.class)
    public void recharge(BigDecimal amount, String payMethod) {
        if (amount == null || amount.signum() <= 0) {
            throw BusinessException.of(400, "充值金额必须大于 0");
        }
        if (payMethod == null || payMethod.isBlank()) {
            throw BusinessException.of(400, "请选择支付方式");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw BusinessException.of(401, "请先登录");
        }
        if (!lockService.lockUserBalance(userId)) {
            throw BusinessException.of("系统繁忙，请稍后重试");
        }
        try {
            User user = userMapper.findById(userId);
            if (user == null || user.getBalance() == null) {
                throw BusinessException.of("用户账户不存在");
            }

            BigDecimal beforeBalance = user.getBalance();
            BigDecimal afterBalance = beforeBalance.add(amount);
            if (userMapper.updateBalanceWithCheck(userId, beforeBalance, afterBalance) == 0) {
                throw BusinessException.of("余额更新失败，请重试");
            }

            RechargeRecord record = new RechargeRecord();
            record.setUserId(userId);
            record.setAmount(amount);
            record.setBeforeBalance(beforeBalance);
            record.setAfterBalance(afterBalance);
            record.setPayMethod(payMethod.trim());
            record.setStatus(1); // 成功
            rechargeRecordMapper.insert(record);
        } finally {
            lockService.unlockUserBalance(userId);
        }
    }

    public List<RechargeRecord> findByUserId(Long userId) {
        return rechargeRecordMapper.findByUserId(userId);
    }

    public PageResult<RechargeRecord> findPage(RechargeRecord record, int pageNum, int pageSize) {
        Long total = rechargeRecordMapper.count(record);
        List<RechargeRecord> list = rechargeRecordMapper.findList(record);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }
}
