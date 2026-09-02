package com.vaccine.service;

import com.vaccine.entity.Vaccine;
import com.vaccine.entity.VaccineCategory;
import com.vaccine.entity.Community;
import com.vaccine.mapper.VaccineMapper;
import com.vaccine.mapper.VaccineCategoryMapper;
import com.vaccine.mapper.CommunityMapper;
import com.vaccine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 * 缓存热点数据：疫苗列表、分类列表、社区列表等
 */
@Slf4j
@Service
public class CacheService {

    // 缓存Key前缀
    private static final String VACCINE_LIST_KEY = "cache:vaccine:list";
    private static final String VACCINE_DETAIL_KEY = "cache:vaccine:detail:";
    private static final String VACCINE_HOT_KEY = "cache:vaccine:hot";
    private static final String CATEGORY_LIST_KEY = "cache:category:list";
    private static final String COMMUNITY_LIST_KEY = "cache:community:list";
    private static final String STATISTICS_KEY = "cache:statistics";

    // 缓存过期时间（分钟）
    private static final long CACHE_EXPIRE_MINUTES = 10;
    private static final long HOT_CACHE_EXPIRE_MINUTES = 5;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private VaccineMapper vaccineMapper;

    @Autowired
    private VaccineCategoryMapper categoryMapper;

    @Autowired
    private CommunityMapper communityMapper;

    // ==================== 疫苗缓存 ====================

    /**
     * 获取疫苗列表（带缓存）
     */
    @SuppressWarnings("unchecked")
    public List<Vaccine> getVaccineList() {
        // 先从缓存获取
        Object cached = redisUtils.get(VACCINE_LIST_KEY);
        if (cached != null) {
            log.debug("从缓存获取疫苗列表");
            return (List<Vaccine>) cached;
        }

        // 缓存未命中，从数据库查询
        Vaccine query = new Vaccine();
        query.setStatus(1);
        List<Vaccine> list = vaccineMapper.findList(query);

        // 存入缓存
        redisUtils.set(VACCINE_LIST_KEY, list, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("疫苗列表已缓存，数量：{}", list.size());

        return list;
    }

    /**
     * 获取疫苗详情（带缓存）
     */
    public Vaccine getVaccineDetail(Long id) {
        String key = VACCINE_DETAIL_KEY + id;
        
        // 先从缓存获取
        Object cached = redisUtils.get(key);
        if (cached != null) {
            log.debug("从缓存获取疫苗详情：{}", id);
            return (Vaccine) cached;
        }

        // 缓存未命中，从数据库查询
        Vaccine vaccine = vaccineMapper.findById(id);
        if (vaccine != null) {
            redisUtils.set(key, vaccine, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("疫苗详情已缓存：{}", id);
        }

        return vaccine;
    }

    /**
     * 获取热门疫苗（带缓存）
     */
    @SuppressWarnings("unchecked")
    public List<Vaccine> getHotVaccines(int limit) {
        String key = VACCINE_HOT_KEY + ":" + limit;
        
        Object cached = redisUtils.get(key);
        if (cached != null) {
            log.debug("从缓存获取热门疫苗");
            return (List<Vaccine>) cached;
        }

        List<Vaccine> list = vaccineMapper.findHotVaccines(limit);
        redisUtils.set(key, list, HOT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("热门疫苗已缓存，数量：{}", list.size());

        return list;
    }

    /**
     * 清除疫苗缓存
     */
    public void clearVaccineCache() {
        redisUtils.deleteByPrefix("cache:vaccine:");
        log.info("疫苗缓存已清除");
    }

    /**
     * 清除单个疫苗缓存
     */
    public void clearVaccineCache(Long id) {
        redisUtils.delete(VACCINE_DETAIL_KEY + id);
        redisUtils.delete(VACCINE_LIST_KEY);
        redisUtils.deleteByPrefix(VACCINE_HOT_KEY);
        log.info("疫苗缓存已清除：{}", id);
    }

    // ==================== 分类缓存 ====================

    /**
     * 获取分类列表（带缓存）
     */
    @SuppressWarnings("unchecked")
    public List<VaccineCategory> getCategoryList() {
        Object cached = redisUtils.get(CATEGORY_LIST_KEY);
        if (cached != null) {
            log.debug("从缓存获取分类列表");
            return (List<VaccineCategory>) cached;
        }

        VaccineCategory query = new VaccineCategory();
        query.setStatus(1);
        List<VaccineCategory> list = categoryMapper.findList(query);
        
        redisUtils.set(CATEGORY_LIST_KEY, list, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("分类列表已缓存，数量：{}", list.size());

        return list;
    }

    /**
     * 清除分类缓存
     */
    public void clearCategoryCache() {
        redisUtils.delete(CATEGORY_LIST_KEY);
        log.info("分类缓存已清除");
    }

    // ==================== 社区缓存 ====================

    /**
     * 获取社区列表（带缓存）
     */
    @SuppressWarnings("unchecked")
    public List<Community> getCommunityList() {
        Object cached = redisUtils.get(COMMUNITY_LIST_KEY);
        if (cached != null) {
            log.debug("从缓存获取社区列表");
            return (List<Community>) cached;
        }

        Community query = new Community();
        query.setStatus(1);
        List<Community> list = communityMapper.findList(query);
        
        redisUtils.set(COMMUNITY_LIST_KEY, list, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("社区列表已缓存，数量：{}", list.size());

        return list;
    }

    /**
     * 清除社区缓存
     */
    public void clearCommunityCache() {
        redisUtils.delete(COMMUNITY_LIST_KEY);
        log.info("社区缓存已清除");
    }

    // ==================== 统计缓存 ====================

    /**
     * 获取统计数据（带缓存）
     */
    public Object getStatistics(String key) {
        return redisUtils.hGet(STATISTICS_KEY, key);
    }

    /**
     * 设置统计数据
     */
    public void setStatistics(String key, Object value) {
        redisUtils.hSet(STATISTICS_KEY, key, value);
        redisUtils.expire(STATISTICS_KEY, HOT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 清除统计缓存
     */
    public void clearStatisticsCache() {
        redisUtils.delete(STATISTICS_KEY);
        log.info("统计缓存已清除");
    }

    // ==================== 清除所有缓存 ====================

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        redisUtils.deleteByPrefix("cache:");
        log.info("所有缓存已清除");
    }
}
