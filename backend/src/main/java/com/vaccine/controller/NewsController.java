package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.News;
import com.vaccine.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资讯控制器
 */
@Tag(name = "资讯管理")
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Operation(summary = "获取资讯详情")
    @GetMapping("/{id}")
    public Result<News> getById(@PathVariable Long id) {
        return Result.success(newsService.findById(id));
    }

    @Operation(summary = "分页查询资讯")
    @GetMapping("/page")
    public Result<PageResult<News>> page(News news,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(newsService.findPage(news, pageNum, pageSize));
    }

    @Operation(summary = "获取最新资讯")
    @GetMapping("/latest")
    public Result<List<News>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(newsService.findLatest(limit));
    }

    @Operation(summary = "获取热门资讯（浏览量最高）")
    @GetMapping("/top")
    public Result<News> top() {
        return Result.success(newsService.findTopByViewCount());
    }

    @Operation(summary = "新增/修改资讯")
    @PostMapping
    @RequireRole("admin")
    public Result<?> save(@RequestBody News news) {
        newsService.save(news);
        return Result.success();
    }

    @Operation(summary = "删除资讯")
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        newsService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "审核资讯")
    @PutMapping("/{id}/audit/{status}")
    @RequireRole("admin")
    public Result<?> audit(@PathVariable Long id, @PathVariable Integer status) {
        newsService.audit(id, status);
        return Result.success();
    }
}
