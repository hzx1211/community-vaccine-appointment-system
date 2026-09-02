package com.vaccine.service;

import com.vaccine.utils.RedisUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DistributedLockServiceTest {

    private DistributedLockService lockService;
    private RedisUtils redisUtils;

    @BeforeEach
    void setUp() {
        lockService = new DistributedLockService();
        redisUtils = mock(RedisUtils.class);
        ReflectionTestUtils.setField(lockService, "redisUtils", redisUtils);
        when(redisUtils.setStringIfAbsent(anyString(), anyString(), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        when(redisUtils.deleteIfValueMatches(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void releasesNestedLocksWithTheirOwnTokens() {
        assertThat(lockService.tryLock("appointment:1:2", 30)).isTrue();
        assertThat(lockService.tryLock("vaccine:stock:2", 5)).isTrue();

        assertThat(lockService.unlock("vaccine:stock:2")).isTrue();
        assertThat(lockService.unlock("appointment:1:2")).isTrue();

        verify(redisUtils).deleteIfValueMatches(eq("lock:vaccine:stock:2"), anyString());
        verify(redisUtils).deleteIfValueMatches(eq("lock:appointment:1:2"), anyString());
    }
}
