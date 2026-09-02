package com.vaccine.interceptor;

import cn.hutool.core.util.StrUtil;
import com.vaccine.common.UserContext;
import com.vaccine.service.RateLimitService;
import com.vaccine.service.TokenService;
import com.vaccine.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 集成Token验证和接口限流
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // IP限流检查
        String clientIp = getClientIp(request);
        if (!rateLimitService.isIpAllowed(clientIp)) {
            log.warn("IP限流触发：{}", clientIp);
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后重试\"}");
            return false;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        // 去掉Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // 去掉Bearer前缀 获取Token
        }

        try {
            // 使用TokenService验证Token（包含Redis验证）
            if (!tokenService.validateToken(token)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"登录已过期或已在其他设备登录\"}");
                return false;
            }

            // 将用户信息存入ThreadLocal
            Long userId = jwtUtils.getUserId(token);
            String username = jwtUtils.getUsername(token);
            String role = jwtUtils.getRole(token);
            UserContext.set(userId, username, role);

            // 管理员跳过用户级别限流检查
            if (!UserContext.isAdmin()) {
                // 普通用户进行限流检查
                if (!rateLimitService.isUserAllowed(userId)) {
                    log.warn("用户限流触发：{}", userId);
                    response.setStatus(429);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后重试\"}");
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Token验证异常", e);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
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
        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
