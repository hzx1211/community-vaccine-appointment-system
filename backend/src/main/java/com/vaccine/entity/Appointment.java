package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录实体
 */
@Data
@Schema(description = "预约记录")
public class Appointment {
    
    @Schema(description = "预约ID")
    private Long id;
    
    @Schema(description = "预约单号")
    private String orderNo;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "疫苗ID")
    @NotNull(message = "请选择疫苗")
    @Positive(message = "疫苗参数不合法")
    private Long vaccineId;
    
    @Schema(description = "接种社区ID")
    @NotNull(message = "请选择接种社区")
    @Positive(message = "社区参数不合法")
    private Long communityId;
    
    @Schema(description = "预约日期")
    @NotNull(message = "请选择预约日期")
    @FutureOrPresent(message = "预约日期不能早于今天")
    private LocalDate appointmentDate;
    
    @Schema(description = "预约时段")
    @NotBlank(message = "请选择预约时段")
    @Size(max = 50, message = "预约时段长度不能超过 50 个字符")
    private String timeSlot;
    
    @Schema(description = "第几剂")
    @NotNull(message = "请选择接种剂次")
    @Positive(message = "接种剂次必须大于 0")
    private Integer doseNumber;
    
    @Schema(description = "支付金额")
    private BigDecimal amount;
    
    @Schema(description = "状态：0-待审核，1-审核通过，2-审核拒绝，3-已支付，4-已接种，5-已取消")
    private Integer status;
    
    @Schema(description = "拒绝原因")
    private String rejectReason;
    
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    
    @Schema(description = "接种时间")
    private LocalDateTime vaccinateTime;
    
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "疫苗名称")
    private String vaccineName;
    
    @Schema(description = "社区名称")
    private String communityName;
    
    @Schema(description = "排除待审核状态（系统管理员查询专用）")
    private Boolean excludePendingStatus;
}
