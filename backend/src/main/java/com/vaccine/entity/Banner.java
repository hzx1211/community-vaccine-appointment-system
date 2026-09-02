package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@Schema(description = "轮播图")
public class Banner {
    
    @Schema(description = "轮播图ID")
    private Long id;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "图片地址")
    private String image;
    
    @Schema(description = "跳转链接")
    private String link;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
