package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Vaccine;
import com.vaccine.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疫苗控制器
 */
@Tag(name = "疫苗管理")
@RestController
@RequestMapping("/api/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @Operation(summary = "获取疫苗详情")
    @GetMapping("/{id}")
    public Result<Vaccine> getById(@PathVariable Long id) {
        return Result.success(vaccineService.findById(id));
    }

    @Operation(summary = "分页查询疫苗")
    @GetMapping("/page")
    public Result<PageResult<Vaccine>> page(Vaccine vaccine,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(vaccineService.findPage(vaccine, pageNum, pageSize));
    }

    @Operation(summary = "获取所有疫苗")
    @GetMapping("/list")
    public Result<List<Vaccine>> list(Vaccine vaccine) {
        return Result.success(vaccineService.findAll(vaccine));
    }

    @Operation(summary = "新增/修改疫苗")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody Vaccine vaccine) {
        vaccineService.save(vaccine);
        return Result.success();
    }

    @Operation(summary = "删除疫苗")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        vaccineService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "更新疫苗状态")
    @PutMapping("/{id}/status/{status}")
    @RequireRole("admin")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        vaccineService.updateStatus(id, status);
        return Result.success();
    }
}
