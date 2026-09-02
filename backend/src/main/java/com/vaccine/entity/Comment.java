package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@Schema(description = "评论信息")
public class Comment {
    
    @Schema(description = "评论ID")
    private Long id;
    
    @Schema(description = "资讯ID")
    private Long newsId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "父评论ID（用于回复）")
    private Long parentId;
    
    @Schema(description = "状态：0-待审核，1-已发布，2-已删除")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    // 非数据库字段
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "被回复用户名")
    private String replyUsername;
}
