package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.VaccineCategory;
import com.vaccine.service.VaccineCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疫苗分类控制器
 */
@Tag(name = "疫苗分类管理")
@RestController
@RequestMapping("/api/vaccine-category")
public class VaccineCategoryController {

    @Autowired
    private VaccineCategoryService vaccineCategoryService;

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<VaccineCategory> getById(@PathVariable Long id) {
        return Result.success(vaccineCategoryService.findById(id));
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<PageResult<VaccineCategory>> page(VaccineCategory category,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(vaccineCategoryService.findPage(category, pageNum, pageSize));
    }

    @Operation(summary = "获取所有启用的分类")
    @GetMapping("/list")
    public Result<List<VaccineCategory>> list() {
        return Result.success(vaccineCategoryService.findAllEnabled());
    }

    @Operation(summary = "新增/修改分类")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody VaccineCategory category) {
        vaccineCategoryService.save(category);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        vaccineCategoryService.deleteById(id);
        return Result.success();
    }
}
