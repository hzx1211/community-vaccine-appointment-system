package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区实体
 */
@Data
@Schema(description = "社区信息")
public class Community {
    
    @Schema(description = "社区ID")
    private Long id;
    
    @Schema(description = "社区名称")
    private String name;
    
    @Schema(description = "社区地址")
    private String address;
    
    @Schema(description = "联系电话")
    private String contactPhone;
    
    @Schema(description = "社区描述")
    private String description;
    
    @Schema(description = "社区图片")
    private String image;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @Schema(description = "社区管理员用户名")
    private String adminUsername;
    
    @Schema(description = "社区管理员真实姓名")
    private String adminRealName;
}
