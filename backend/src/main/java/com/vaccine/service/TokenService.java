package com.vaccine.service;

import com.vaccine.utils.JwtUtils;
import com.vaccine.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token服务
 * 使用Redis存储Token，支持主动登出和Token续期
 */
@Service
public class TokenService {

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_TOKEN_PREFIX = "user_token:";

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 生成Token并存储到Redis
     */
    public String createToken(Long userId, String username, String role) {
        // 生成JWT Token ，并把Token存储到Redis中，用于后续校验和单点登录控制

        String token = jwtUtils.generateToken(userId, username, role);
        
        // 存储Token到Redis，key为token:userId
        String tokenKey = TOKEN_PREFIX + userId;
        String userTokenKey = USER_TOKEN_PREFIX + token;
        
        // 存储用户ID -> Token的映射（用于单点登录，踢掉旧Token）
        // TODO: 压测时临时关闭踢掉旧Token逻辑，压测完成后恢复
        String oldToken = redisUtils.getString(tokenKey);
        if (oldToken != null) {
            // 删除旧Token
            redisUtils.delete(USER_TOKEN_PREFIX + oldToken);
        }
        
        // 存储新Token
        redisUtils.setString(tokenKey, token, expiration, TimeUnit.MILLISECONDS);
        // 存储Token -> 用户ID的映射（用于验证Token）
        redisUtils.setString(userTokenKey, String.valueOf(userId), expiration, TimeUnit.MILLISECONDS);
        
        return token;
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            // 1. 验证JWT本身是否有效
            if (jwtUtils.isTokenExpired(token)) {
                return false;
            }
            
            // 2. 验证Token是否在Redis中存在（支持主动登出）
            String userTokenKey = USER_TOKEN_PREFIX + token;
            String userId = redisUtils.getString(userTokenKey);
            if (userId == null) {
                return false;
            }
            
            // TODO: 压测时临时关闭单点登录校验，压测完成后恢复
            // 3. 验证是否是当前有效的Token（单点登录）
            String tokenKey = TOKEN_PREFIX + userId;
            String currentToken = redisUtils.getString(tokenKey);
            return token.equals(currentToken);
            // return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新Token（续期）
     */
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            return null;
        }
        
        try {
            Long userId = jwtUtils.getUserId(token);
            String username = jwtUtils.getUsername(token);
            String role = jwtUtils.getRole(token);
            
            // 删除旧Token
            removeToken(token);
            
            // 生成新Token
            return createToken(userId, username, role);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除Token（登出）
     */
    public void removeToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        try {
            Long userId = jwtUtils.getUserId(token);
            String tokenKey = TOKEN_PREFIX + userId;
            String userTokenKey = USER_TOKEN_PREFIX + token;
            
            redisUtils.delete(tokenKey);
            redisUtils.delete(userTokenKey);
        } catch (Exception e) {
            // 忽略异常
        }
    }

    /**
     * 根据用户ID删除Token（强制登出）
     */
    public void removeTokenByUserId(Long userId) {
        String tokenKey = TOKEN_PREFIX + userId;
        String token = redisUtils.getString(tokenKey);
        
        if (token != null) {
            redisUtils.delete(USER_TOKEN_PREFIX + token);
        }
        redisUtils.delete(tokenKey);
    }

    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getTokenExpireTime(String token) {
        String userTokenKey = USER_TOKEN_PREFIX + token;
        return redisUtils.getExpire(userTokenKey);
    }
}
