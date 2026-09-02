package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录实体
 */
@Data
@Schema(description = "充值记录")
public class RechargeRecord {
    
    @Schema(description = "记录ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "充值金额")
    private BigDecimal amount;
    
    @Schema(description = "充值前余额")
    private BigDecimal beforeBalance;
    
    @Schema(description = "充值后余额")
    private BigDecimal afterBalance;
    
    @Schema(description = "支付方式")
    private String payMethod;
    
    @Schema(description = "状态：0-失败，1-成功")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    // 非数据库字段
    @Schema(description = "用户名")
    private String username;
}
