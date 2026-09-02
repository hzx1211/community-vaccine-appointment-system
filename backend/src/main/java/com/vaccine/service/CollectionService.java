package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.common.UserContext;
import com.vaccine.entity.UserCollection;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.CollectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务
 */
@Service
public class CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    /**
     * 添加收藏
     */
    public void add(Long vaccineId) {
        Long userId = UserContext.getUserId();
        
        // 检查是否已收藏
        int count = collectionMapper.countByUserAndVaccine(userId, vaccineId);
        if (count > 0) {
            throw BusinessException.of("已收藏该疫苗");
        }
        
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setVaccineId(vaccineId);
        collectionMapper.insert(collection);
    }

    /**
     * 取消收藏
     */
    public void remove(Long vaccineId) {
        Long userId = UserContext.getUserId();
        collectionMapper.deleteByUserAndVaccine(userId, vaccineId);
    }

    /**
     * 检查是否已收藏
     */
    public boolean isCollected(Long vaccineId) {
        Long userId = UserContext.getUserId();
        return collectionMapper.countByUserAndVaccine(userId, vaccineId) > 0;
    }

    public List<UserCollection> findByUserId(Long userId) {
        return collectionMapper.findByUserId(userId);
    }

    public PageResult<UserCollection> findPage(UserCollection collection, int pageNum, int pageSize) {
        Long total = collectionMapper.count(collection);
        List<UserCollection> list = collectionMapper.findList(collection);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }
}
