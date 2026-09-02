package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Admin;
import com.vaccine.entity.Community;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社区控制器
 */
@Tag(name = "社区管理")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AdminMapper adminMapper;

    @Operation(summary = "获取社区详情")
    @GetMapping("/{id}")
    public Result<Community> getById(@PathVariable Long id) {
        return Result.success(communityService.findById(id));
    }

    @Operation(summary = "分页查询社区")
    @GetMapping("/page")
    public Result<PageResult<Community>> page(Community community,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(communityService.findPage(community, pageNum, pageSize));
    }

    @Operation(summary = "获取所有启用的社区")
    @GetMapping("/list")
    public Result<List<Community>> list() {
        return Result.success(communityService.findAllEnabled());
    }

    @Operation(summary = "新增/修改社区")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody Community community) {
        communityService.save(community);
        return Result.success();
    }

    @Operation(summary = "删除社区")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        communityService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "批量删除社区")
    @DeleteMapping("/batch")
    @RequireRole("admin")
    public Result<?> deleteBatch(@RequestBody List<Long> ids) {
        communityService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新社区状态")
    @PutMapping("/{id}/status/{status}")
    @RequireRole("admin")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        communityService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取当前社区管理员的社区")
    @GetMapping("/my")
    @RequireRole("community_admin")
    public Result<Community> getMyCommunity() {
        if (!UserContext.isCommunityAdmin()) {
            throw BusinessException.of(403, "无权限访问");
        }
        Admin admin = adminMapper.findById(UserContext.getUserId());
        if (admin.getCommunityId() == null) {
            return Result.success(null);
        }
        return Result.success(communityService.findById(admin.getCommunityId()));
    }

    @Operation(summary = "获取当前社区管理员的社区统计")
    @GetMapping("/my/stats")
    @RequireRole("community_admin")
    public Result<Map<String, Object>> getMyCommunityStats() {
        if (!UserContext.isCommunityAdmin()) {
            throw BusinessException.of(403, "无权限访问");
        }
        Admin admin = adminMapper.findById(UserContext.getUserId());
        if (admin.getCommunityId() == null) {
            return Result.success(Map.of());
        }
        return Result.success(communityService.getCommunityStats(admin.getCommunityId()));
    }
}
