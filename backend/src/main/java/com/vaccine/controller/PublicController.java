package com.vaccine.controller;

import com.vaccine.common.Result;
import com.vaccine.entity.*;
import com.vaccine.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开接口控制器（无需登录）
 */
@Tag(name = "公开接口")
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private VaccineCategoryService vaccineCategoryService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private CommunityService communityService;

    @Operation(summary = "获取首页数据")
    @GetMapping("/home")
    public Result<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("banners", bannerService.findAllEnabled());
        data.put("hotVaccines", vaccineService.findHotVaccines(6));
        data.put("latestNews", newsService.findLatest(5));
        data.put("announcements", announcementService.findAllEnabled());
        return Result.success(data);
    }

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        return Result.success(bannerService.findAllEnabled());
    }

    @Operation(summary = "获取热门疫苗")
    @GetMapping("/hot-vaccines")
    public Result<List<Vaccine>> hotVaccines(@RequestParam(defaultValue = "6") int limit) {
        return Result.success(vaccineService.findHotVaccines(limit));
    }

    @Operation(summary = "获取疫苗分类列表")
    @GetMapping("/vaccine-categories")
    public Result<List<VaccineCategory>> vaccineCategories() {
        return Result.success(vaccineCategoryService.findAllEnabled());
    }

    @Operation(summary = "获取最新资讯")
    @GetMapping("/latest-news")
    public Result<List<News>> latestNews(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(newsService.findLatest(limit));
    }

    @Operation(summary = "获取系统公告")
    @GetMapping("/announcements")
    public Result<List<Announcement>> announcements() {
        return Result.success(announcementService.findAllEnabled());
    }

    @Operation(summary = "获取社区列表")
    @GetMapping("/communities")
    public Result<List<Community>> communities() {
        return Result.success(communityService.findAllEnabled());
    }
}
