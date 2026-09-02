package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Announcement;
import com.vaccine.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@Tag(name = "公告管理")
@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        return Result.success(announcementService.findById(id));
    }

    @Operation(summary = "分页查询公告")
    @GetMapping("/page")
    public Result<PageResult<Announcement>> page(Announcement announcement,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(announcementService.findPage(announcement, pageNum, pageSize));
    }

    @Operation(summary = "获取所有启用的公告")
    @GetMapping("/list")
    public Result<List<Announcement>> list() {
        return Result.success(announcementService.findAllEnabled());
    }

    @Operation(summary = "新增/修改公告")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return Result.success();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        announcementService.deleteById(id);
        return Result.success();
    }
}
