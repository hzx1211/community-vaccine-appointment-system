package com.vaccine.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 分页结果
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "数据列表")
    private List<T> list;

    public PageResult() {}

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public static <T> PageResult<T> of(Long total, List<T> list) {
        return new PageResult<>(total, list);
    }
}
