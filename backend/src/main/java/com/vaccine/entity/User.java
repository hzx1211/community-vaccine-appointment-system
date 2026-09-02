package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 普通用户实体
 */
@Data
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "身份证号")
    private String idCard;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "账户余额")
    private BigDecimal balance;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
