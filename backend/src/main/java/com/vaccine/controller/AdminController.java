package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Admin;
import com.vaccine.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@Tag(name = "管理员管理")
@RestController
@RequestMapping("/api/admin")
@RequireRole("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "获取管理员详情")
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        return Result.success(adminService.findById(id));
    }

    @Operation(summary = "分页查询管理员")
    @GetMapping("/page")
    public Result<PageResult<Admin>> page(Admin admin,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminService.findPage(admin, pageNum, pageSize));
    }

    @Operation(summary = "新增/修改管理员")
    @PostMapping
    public Result<?> save(@RequestBody Admin admin) {
        adminService.save(admin);
        return Result.success();
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        adminService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "更新管理员状态")
    @PutMapping("/{id}/status/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        adminService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }
}
