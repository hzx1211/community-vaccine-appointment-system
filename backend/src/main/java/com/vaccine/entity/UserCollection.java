package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
@Schema(description = "用户收藏")
public class UserCollection {
    
    @Schema(description = "收藏ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "疫苗ID")
    private Long vaccineId;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    // 非数据库字段
    @Schema(description = "疫苗名称")
    private String vaccineName;
    
    @Schema(description = "疫苗图片")
    private String vaccineImage;
    
    @Schema(description = "疫苗价格")
    private java.math.BigDecimal vaccinePrice;
}
