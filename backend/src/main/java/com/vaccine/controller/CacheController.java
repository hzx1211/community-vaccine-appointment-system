package com.vaccine.controller;

import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存管理控制器
 */
@Tag(name = "缓存管理")
@RestController
@RequestMapping("/api/admin/cache")
@RequireRole("admin")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Operation(summary = "清除所有缓存")
    @DeleteMapping("/all")
    public Result<?> clearAllCache() {
        cacheService.clearAllCache();
        return Result.success("所有缓存已清除");
    }

    @Operation(summary = "清除疫苗缓存")
    @DeleteMapping("/vaccine")
    public Result<?> clearVaccineCache() {
        cacheService.clearVaccineCache();
        return Result.success("疫苗缓存已清除");
    }

    @Operation(summary = "清除分类缓存")
    @DeleteMapping("/category")
    public Result<?> clearCategoryCache() {
        cacheService.clearCategoryCache();
        return Result.success("分类缓存已清除");
    }

    @Operation(summary = "清除社区缓存")
    @DeleteMapping("/community")
    public Result<?> clearCommunityCache() {
        cacheService.clearCommunityCache();
        return Result.success("社区缓存已清除");
    }

    @Operation(summary = "清除统计缓存")
    @DeleteMapping("/statistics")
    public Result<?> clearStatisticsCache() {
        cacheService.clearStatisticsCache();
        return Result.success("统计缓存已清除");
    }
}
