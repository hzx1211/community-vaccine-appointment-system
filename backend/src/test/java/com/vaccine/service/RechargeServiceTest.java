package com.vaccine.service;

import com.vaccine.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RechargeServiceTest {

    private final RechargeService rechargeService = new RechargeService();

    @Test
    void rejectsZeroAndNegativeRechargeAmounts() {
        assertThatThrownBy(() -> rechargeService.recharge(BigDecimal.ZERO, "balance"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("必须大于 0");
        assertThatThrownBy(() -> rechargeService.recharge(new BigDecimal("-1"), "balance"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("必须大于 0");
    }
}
