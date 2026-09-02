package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.UserCollection;
import com.vaccine.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 */
@Tag(name = "收藏管理")
@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Operation(summary = "我的收藏列表")
    @GetMapping("/my")
    @RequireRole("user")
    public Result<List<UserCollection>> myCollections() {
        Long userId = UserContext.getUserId();
        return Result.success(collectionService.findByUserId(userId));
    }

    @Operation(summary = "分页查询收藏（管理员）")
    @GetMapping("/page")
    @RequireRole("admin")
    public Result<PageResult<UserCollection>> page(UserCollection collection,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(collectionService.findPage(collection, pageNum, pageSize));
    }

    @Operation(summary = "添加收藏")
    @PostMapping("/{vaccineId}")
    @RequireRole("user")
    public Result<?> add(@PathVariable Long vaccineId) {
        collectionService.add(vaccineId);
        return Result.success("收藏成功");
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{vaccineId}")
    @RequireRole("user")
    public Result<?> remove(@PathVariable Long vaccineId) {
        collectionService.remove(vaccineId);
        return Result.success("取消收藏成功");
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{vaccineId}")
    @RequireRole("user")
    public Result<Boolean> check(@PathVariable Long vaccineId) {
        return Result.success(collectionService.isCollected(vaccineId));
    }
}
