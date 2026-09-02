package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@Schema(description = "系统公告")
public class Announcement {
    
    @Schema(description = "公告ID")
    private Long id;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
