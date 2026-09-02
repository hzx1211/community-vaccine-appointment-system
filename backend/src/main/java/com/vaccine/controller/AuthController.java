package com.vaccine.controller;

import com.vaccine.common.Result;
import com.vaccine.common.UserContext;
import com.vaccine.entity.Admin;
import com.vaccine.entity.User;
import com.vaccine.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody @Validated RegisterDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        authService.register(dto.getUsername(), dto.getPassword(), dto.getRealName(), dto.getPhone(), clientIp);
        return Result.success("注册成功");
    }

    @Operation(summary = "用户登录（自动判断用户类型）")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        Map<String, Object> result = authService.login(dto.getUsername(), dto.getPassword(), clientIp);
        return Result.success(result);
    }

    @Operation(summary = "普通用户登录")
    @PostMapping("/login/user")
    public Result<Map<String, Object>> loginUser(@RequestBody @Validated LoginDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        Map<String, Object> result = authService.loginUser(dto.getUsername(), dto.getPassword(), clientIp);
        return Result.success(result);
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login/admin")
    public Result<Map<String, Object>> loginAdmin(@RequestBody @Validated LoginDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        Map<String, Object> result = authService.loginAdmin(dto.getUsername(), dto.getPassword(), clientIp);
        return Result.success(result);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        authService.logout(token);
        return Result.success("登出成功");
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String newToken = authService.refreshToken(token);
        if (newToken == null) {
            return Result.error("Token刷新失败");
        }
        return Result.success(newToken);
    }

    @Operation(summary = "获取当前用户信息（根据角色返回对应信息）")
    @GetMapping("/info")
    public Result<?> getCurrentUserInfo() {
        String role = UserContext.getRole();
        if ("user".equals(role)) {
            User user = authService.getCurrentUserWithDecrypt();
            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("phone", user.getPhone());
            result.put("email", user.getEmail());
            result.put("idCard", user.getIdCard());
            result.put("avatar", user.getAvatar());
            result.put("balance", user.getBalance());
            result.put("role", "user");
            result.put("status", user.getStatus());
            return Result.success(result);
        } else {
            Admin admin = authService.getCurrentAdmin();
            Map<String, Object> result = new HashMap<>();
            result.put("id", admin.getId());
            result.put("username", admin.getUsername());
            result.put("realName", admin.getRealName());
            result.put("phone", admin.getPhone());
            result.put("email", admin.getEmail());
            result.put("avatar", admin.getAvatar());
            result.put("role", admin.getRole());
            result.put("communityId", admin.getCommunityId());
            result.put("status", admin.getStatus());
            return Result.success(result);
        }
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<?> updatePassword(@RequestBody @Validated PasswordDTO dto) {
        authService.updatePassword(dto.getOldPassword(), dto.getNewPassword());
        return Result.success("密码修改成功，请重新登录");
    }

    @Operation(summary = "更新个人信息（普通用户）")
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        authService.updateProfile(user);
        return Result.success("更新成功");
    }

    @Operation(summary = "更新个人信息（管理员）")
    @PutMapping("/profile/admin")
    public Result<?> updateAdminProfile(@RequestBody Admin admin) {
        authService.updateAdminProfile(admin);
        return Result.success("更新成功");
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Data
    public static class RegisterDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String realName;
        private String phone;
    }

    @Data
    public static class LoginDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class PasswordDTO {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}
