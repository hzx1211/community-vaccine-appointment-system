package com.vaccine.controller;

import com.vaccine.common.PageResult;
import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.RechargeRecord;
import com.vaccine.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值控制器
 */
@Tag(name = "充值管理")
@RestController
@RequestMapping("/api/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @Operation(summary = "用户充值")
    @PostMapping
    @RequireRole("user")
    public Result<?> recharge(@Valid @RequestBody RechargeDTO dto) {
        rechargeService.recharge(dto.getAmount(), dto.getPayMethod());
        return Result.success("充值成功");
    }

    @Operation(summary = "我的充值记录")
    @GetMapping("/my")
    @RequireRole("user")
    public Result<List<RechargeRecord>> myRecords() {
        Long userId = UserContext.getUserId();
        return Result.success(rechargeService.findByUserId(userId));
    }

    @Operation(summary = "分页查询充值记录（管理员）")
    @GetMapping("/page")
    @RequireRole("admin")
    public Result<PageResult<RechargeRecord>> page(RechargeRecord record,
                                                   @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(rechargeService.findPage(record, pageNum, pageSize));
    }

    @Data
    public static class RechargeDTO {
        @NotNull(message = "充值金额不能为空")
        @DecimalMin(value = "0.01", message = "充值金额必须大于 0")
        private BigDecimal amount;

        @NotBlank(message = "请选择支付方式")
        @Size(max = 32, message = "支付方式长度不能超过 32 个字符")
        private String payMethod;
    }
}
