package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.News;
import com.vaccine.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资讯服务
 */
@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    public News findById(Long id) {
        News news = newsMapper.findById(id);
        if (news != null) {
            newsMapper.increaseViewCount(id);
        }
        return news;
    }

    public void save(News news) {
        if (news.getId() == null) {
            news.setViewCount(0);
            news.setStatus(0); // 待审核
            newsMapper.insert(news);
        } else {
            newsMapper.update(news);
        }
    }

    public void deleteById(Long id) {
        newsMapper.deleteById(id);
    }

    public PageResult<News> findPage(News news, int pageNum, int pageSize) {
        Long total = newsMapper.count(news);
        List<News> list = newsMapper.findList(news);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<News> findLatest(int limit) {
        return newsMapper.findLatest(limit);
    }

    /**
     * 获取浏览量最高的资讯
     */
    public News findTopByViewCount() {
        return newsMapper.findTopByViewCount();
    }

    /**
     * 审核资讯
     */
    public void audit(Long id, Integer status) {
        News news = new News();
        news.setId(id);
        news.setStatus(status);
        newsMapper.update(news);
    }
}
