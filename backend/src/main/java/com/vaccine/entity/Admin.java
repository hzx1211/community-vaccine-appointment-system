package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
@Schema(description = "管理员信息") // swagger文档 标记该类在文档中作为模型
public class Admin {
    
    @Schema(description = "管理员ID")
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
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "角色：admin-系统管理员，community_admin-社区管理员")
    private String role;
    
    @Schema(description = "所属社区ID")
    private Long communityId;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @Schema(description = "社区名称")
    private String communityName;
}
