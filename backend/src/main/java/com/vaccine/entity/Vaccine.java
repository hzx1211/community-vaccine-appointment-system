package com.vaccine.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 疫苗实体
 */
@Data
@Schema(description = "疫苗信息")
public class Vaccine {
    
    @Schema(description = "疫苗ID")
    private Long id;
    
    @Schema(description = "疫苗名称")
    private String name;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "生产厂家")
    private String manufacturer;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "库存数量")
    private Integer stock;
    
    @Schema(description = "接种剂次")
    private Integer doses;
    
    @Schema(description = "接种间隔天数")
    private Integer intervalDays;
    
    @Schema(description = "适用人群")
    private String targetGroup;
    
    @Schema(description = "禁忌症")
    private String contraindication;
    
    @Schema(description = "疫苗描述")
    private String description;
    
    @Schema(description = "疫苗图片")
    private String image;
    
    @Schema(description = "预约数量")
    private Integer appointmentCount;
    
    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @Schema(description = "分类名称")
    private String categoryName;
}
