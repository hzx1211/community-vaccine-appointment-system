package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.Announcement;
import com.vaccine.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务
 */
@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    public Announcement findById(Long id) {
        return announcementMapper.findById(id);
    }

    public void save(Announcement announcement) {
        if (announcement.getId() == null) {
            announcement.setStatus(1);
            announcementMapper.insert(announcement);
        } else {
            announcementMapper.update(announcement);
        }
    }

    public void deleteById(Long id) {
        announcementMapper.deleteById(id);
    }

    public PageResult<Announcement> findPage(Announcement announcement, int pageNum, int pageSize) {
        Long total = announcementMapper.count(announcement);
        List<Announcement> list = announcementMapper.findList(announcement);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<Announcement> findAllEnabled() {
        return announcementMapper.findAllEnabled();
    }
}
