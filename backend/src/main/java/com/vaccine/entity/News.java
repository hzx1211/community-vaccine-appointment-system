package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资讯实体
 */
@Data
@Schema(description = "资讯信息")
public class News {
    
    @Schema(description = "资讯ID")
    private Long id;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "简介")
    private String summary;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "封面图")
    private String coverImage;
    
    @Schema(description = "所属社区ID")
    private Long communityId;
    
    @Schema(description = "作者ID")
    private Long authorId;
    
    @Schema(description = "浏览量")
    private Integer viewCount;
    
    @Schema(description = "状态：0-待审核，1-已发布，2-已拒绝")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @Schema(description = "社区名称")
    private String communityName;
    
    @Schema(description = "作者名称")
    private String authorName;
}
