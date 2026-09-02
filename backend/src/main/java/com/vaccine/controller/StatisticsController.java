package com.vaccine.controller;

import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Admin;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.mapper.UserMapper;
import com.vaccine.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 */
@Tag(name = "数据统计")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    @RequireRole({"admin", "community_admin"})
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        
        // 社区管理员只能看到自己社区的统计
        if (UserContext.isCommunityAdmin()) {
            Admin admin = adminMapper.findById(UserContext.getUserId());
            Long communityId = admin.getCommunityId();
            if (communityId != null) {
                data.put("pendingCount", appointmentService.countByCommunityIdAndStatus(communityId, 0));
                data.put("approvedCount", appointmentService.countByCommunityIdAndStatus(communityId, 1));
                data.put("paidCount", appointmentService.countByCommunityIdAndStatus(communityId, 3));
                data.put("vaccinatedCount", appointmentService.countByCommunityIdAndStatus(communityId, 4));
            } else {
                data.put("pendingCount", 0);
                data.put("approvedCount", 0);
                data.put("paidCount", 0);
                data.put("vaccinatedCount", 0);
            }
        } else {
            // 管理员看全部统计
            data.put("pendingCount", appointmentService.countByStatus(0));
            data.put("approvedCount", appointmentService.countByStatus(1));
            data.put("paidCount", appointmentService.countByStatus(3));
            data.put("vaccinatedCount", appointmentService.countByStatus(4));
            // 总收入
            data.put("totalIncome", appointmentService.sumIncome());
            // 总用户数
            data.put("totalUsers", userMapper.countUsers());
        }
        return Result.success(data);
    }

    @Operation(summary = "按日期统计预约数量")
    @GetMapping("/by-date")
    @RequireRole("admin")
    public Result<List<Map<String, Object>>> byDate(@RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return Result.success(appointmentService.countByDate(startDate));
    }

    @Operation(summary = "按社区统计接种数量")
    @GetMapping("/by-community")
    @RequireRole("admin")
    public Result<List<Map<String, Object>>> byCommunity() {
        return Result.success(appointmentService.countByCommunity());
    }

    @Operation(summary = "按疫苗统计接种数量（疫苗接种排行）")
    @GetMapping("/by-vaccine")
    @RequireRole("admin")
    public Result<List<Map<String, Object>>> byVaccine() {
        return Result.success(appointmentService.countByVaccine());
    }

    @Operation(summary = "按日期统计用户增长")
    @GetMapping("/user-growth")
    @RequireRole("admin")
    public Result<List<Map<String, Object>>> userGrowth(@RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return Result.success(userMapper.countUsersByDate(startDate));
    }

    @Operation(summary = "按日期统计收入")
    @GetMapping("/income-by-date")
    @RequireRole("admin")
    public Result<List<Map<String, Object>>> incomeByDate(@RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return Result.success(appointmentService.sumIncomeByDate(startDate));
    }
}
