package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Banner;
import com.vaccine.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    public Result<Banner> getById(@PathVariable Long id) {
        return Result.success(bannerService.findById(id));
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping("/page")
    public Result<PageResult<Banner>> page(Banner banner,
                                           @RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bannerService.findPage(banner, pageNum, pageSize));
    }

    @Operation(summary = "获取所有启用的轮播图")
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerService.findAllEnabled());
    }

    @Operation(summary = "新增/修改轮播图")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        bannerService.deleteById(id);
        return Result.success();
    }
}
