package com.vaccine.config;

import com.vaccine.interceptor.AuthInterceptor;
import com.vaccine.interceptor.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private RoleInterceptor roleInterceptor;

    @Autowired
    private UploadStorage uploadStorage;

    @Value("${cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*}")
    private String allowedOriginPatterns;

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(Arrays.stream(allowedOriginPatterns.split(","))
                        .map(String::trim)
                        .toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/login",        // 登录
                        "/api/auth/login/user",   // 用户登录
                        "/api/auth/login/admin",  // 管理员登录
                        "/api/auth/register",     // 注册
                        "/api/auth/logout",       // 登出
                        "/api/public/**",         // 公开接口
                        "/api/comment/news/**",   // 评论列表（公开）
                        "/uploads/**",            // 静态资源
                        "/doc.html",              // Swagger
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**");
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadStorage.getResourceLocation());
    }
}
