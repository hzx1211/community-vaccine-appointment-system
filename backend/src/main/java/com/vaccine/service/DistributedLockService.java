package com.vaccine.service;

import com.vaccine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务
 * 使用Redis实现分布式锁，用于高并发场景下的库存扣减等操作
 */
@Slf4j
@Service
public class DistributedLockService {

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TIMEOUT = 10; // 默认锁超时时间（秒）
    private static final long DEFAULT_WAIT_TIMEOUT = 5;  // 默认等待超时时间（秒）
    private static final long RETRY_INTERVAL = 100;      // 重试间隔（毫秒）

    @Autowired
    private RedisUtils redisUtils;

    // 每个线程按锁 key 保存 token，支持同一业务中嵌套获取多把锁。
    private static final ThreadLocal<Map<String, String>> LOCK_VALUES = ThreadLocal.withInitial(HashMap::new);

    /**
     * 尝试获取锁（不等待）
     * 
     * @param lockKey 锁的key
     * @param timeoutSeconds 锁的超时时间（秒）
     * @return true-获取成功，false-获取失败
     */
    public boolean tryLock(String lockKey, long timeoutSeconds) {
        String key = LOCK_PREFIX + lockKey;
        String value = UUID.randomUUID().toString();
        
        boolean success = redisUtils.setStringIfAbsent(key, value, timeoutSeconds, TimeUnit.SECONDS);
        
        if (success) {
            LOCK_VALUES.get().put(key, value);
            log.debug("获取分布式锁成功，key：{}", lockKey);
        }
        
        return success;
    }

    /**
     * 尝试获取锁（使用默认超时时间）
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 获取锁（带等待）
     * 
     * @param lockKey 锁的key
     * @param lockTimeoutSeconds 锁的超时时间（秒）
     * @param waitTimeoutSeconds 等待超时时间（秒）
     * @return true-获取成功，false-获取失败
     */
    public boolean lock(String lockKey, long lockTimeoutSeconds, long waitTimeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long waitTimeoutMillis = waitTimeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < waitTimeoutMillis) {
            if (tryLock(lockKey, lockTimeoutSeconds)) {
                return true;
            }
            
            try {
                Thread.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        log.warn("获取分布式锁超时，key：{}", lockKey);
        return false;
    }

    /**
     * 获取锁（使用默认超时时间）
     */
    public boolean lock(String lockKey) {
        return lock(lockKey, DEFAULT_LOCK_TIMEOUT, DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * 释放锁
     * 
     * @param lockKey 锁的key
     * @return true-释放成功，false-释放失败
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        Map<String, String> heldLocks = LOCK_VALUES.get();
        String value = heldLocks.get(key);
        
        if (value == null) {
            log.warn("释放锁失败，未持有锁：{}", lockKey);
            if (heldLocks.isEmpty()) {
                LOCK_VALUES.remove();
            }
            return false;
        }
        
        try {
            // 使用 Lua 脚本原子比较并删除，防止锁超时后误删其他请求的锁。
            if (redisUtils.deleteIfValueMatches(key, value)) {
                log.debug("释放分布式锁成功，key：{}", lockKey);
                return true;
            } else {
                log.warn("释放锁失败，锁已被其他线程持有：{}", lockKey);
                return false;
            }
        } finally {
            heldLocks.remove(key);
            if (heldLocks.isEmpty()) {
                LOCK_VALUES.remove();
            }
        }
    }

    /**
     * 执行带锁的操作
     * 
     * @param lockKey 锁的key
     * @param action 要执行的操作
     * @return 操作是否成功执行
     */
    public boolean executeWithLock(String lockKey, Runnable action) {
        if (tryLock(lockKey)) {
            try {
                action.run();
                return true;
            } finally {
                unlock(lockKey);
            }
        }
        return false;
    }

    /**
     * 执行带锁的操作（带返回值）
     * 
     * @param lockKey 锁的key
     * @param action 要执行的操作
     * @return 操作的返回值，获取锁失败返回null
     */
    public <T> T executeWithLock(String lockKey, java.util.function.Supplier<T> action) {
        if (tryLock(lockKey)) {
            try {
                return action.get();
            } finally {
                unlock(lockKey);
            }
        }
        return null;
    }

    // ==================== 业务锁方法 ====================

    /**
     * 获取疫苗库存锁
     */
    public boolean lockVaccineStock(Long vaccineId) {

        return lock("vaccine:stock:" + vaccineId,
                5, 3);
    }

    /**
     * 释放疫苗库存锁
     */
    public boolean unlockVaccineStock(Long vaccineId) {
        return unlock("vaccine:stock:" + vaccineId);
    }

    /**
     * 获取用户余额锁
     */
    public boolean lockUserBalance(Long userId) {
        return lock
                ("user:balance:" + userId,
                        5, 3);
    }

    /**
     * 释放用户余额锁
     */
    public boolean unlockUserBalance(Long userId) {
        return unlock("user:balance:" + userId);
    }

    /**
     * 获取预约操作锁（防止重复预约）
     */
    public boolean lockAppointment(Long userId, Long vaccineId) {
        return tryLock(
                "appointment:" + userId + ":" + vaccineId,
                30);
    }

    /**
     * 释放预约操作锁
     */
    public boolean unlockAppointment(Long userId, Long vaccineId) {
        return unlock("appointment:" + userId + ":" + vaccineId);
    }
}
