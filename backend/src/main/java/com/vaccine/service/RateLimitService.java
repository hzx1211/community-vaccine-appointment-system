package com.vaccine.service;

import com.vaccine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 接口限流服务
 * 使用Redis实现滑动窗口限流
 */
@Slf4j
@Service
public class RateLimitService {

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 检查是否允许访问（简单计数器限流）
     * 
     * @param key 限流key（如：ip、userId、接口名）
     * @param limit 时间窗口内最大请求数
     * @param windowSeconds 时间窗口（秒）
     * @return true-允许访问，false-被限流
     */
    public boolean isAllowed(String key, int limit, int windowSeconds) {
        // TODO: 压测时临时关闭限流，压测完成后恢复原逻辑
        
        String redisKey = RATE_LIMIT_PREFIX + key;
        
        // 获取当前计数
        String countStr = redisUtils.getString(redisKey);
        
        if (countStr == null) {
            // 第一次访问，设置计数为1
            redisUtils.setString(redisKey, "1", windowSeconds, TimeUnit.SECONDS);
            return true;
        }
        
        int count = Integer.parseInt(countStr);
        if (count >= limit) {
            log.warn("接口限流触发，key：{}，当前计数：{}，限制：{}", key, count, limit);
            return false;
        }
        
        // 计数加1
        redisUtils.increment(redisKey, 1);
        return true;
    }

    /**
     * 检查IP是否允许访问
     * 默认：每秒最多10次请求
     */
    public boolean isIpAllowed(String ip) {
        return isAllowed("ip:" + ip, 10, 1);
    }

    /**
     * 检查用户是否允许访问
     * 默认：每秒最多100次请求
     */
    public boolean isUserAllowed(Long userId) {
        return isAllowed("user:" + userId, 100, 1);
    }

    /**
     * 检查接口是否允许访问
     * 
     * @param api 接口路径
     * @param identifier 标识符（IP或用户ID）
     * @param limit 限制次数
     * @param windowSeconds 时间窗口
     */
    public boolean isApiAllowed(String api, String identifier, int limit, int windowSeconds) {
        String key = "api:" + api + ":" + identifier;
        return isAllowed(key, limit, windowSeconds);
    }

    /**
     * 预约接口限流
     * 每个用户每分钟最多5次预约请求
     */
    public boolean isAppointmentAllowed(Long userId) {

        return isAllowed("appointment:" + userId, 5, 60);
    }

    /**
     * 登录接口限流
     * 每个IP每分钟最多10次登录尝试
     */
    public boolean isLoginAllowed(String ip) {
        return isAllowed("login:" + ip, 10, 60);
    }

    /**
     * 注册接口限流
     * 每个IP每小时最多5次注册
     */
    public boolean isRegisterAllowed(String ip) {
        return isAllowed("register:" + ip, 5, 3600);
    }

    /**
     * 短信验证码限流
     * 每个手机号每分钟最多1次
     */
    public boolean isSmsAllowed(String phone) {
        return isAllowed("sms:" + phone, 1, 60);
    }

    /**
     * 获取剩余可用次数
     */
    public int getRemainingCount(String key, int limit) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        String countStr = redisUtils.getString(redisKey);
        
        if (countStr == null) {
            return limit;
        }
        
        int count = Integer.parseInt(countStr);
        return Math.max(0, limit - count);
    }

    /**
     * 重置限流计数
     */
    public void resetLimit(String key) {
        redisUtils.delete(RATE_LIMIT_PREFIX + key);
    }
}
