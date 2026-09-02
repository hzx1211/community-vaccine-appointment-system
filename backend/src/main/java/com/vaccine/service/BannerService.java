package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.Banner;
import com.vaccine.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务
 */
@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    public Banner findById(Long id) {
        return bannerMapper.findById(id);
    }

    public void save(Banner banner) {
        if (banner.getId() == null) {
            banner.setStatus(1);
            bannerMapper.insert(banner);
        } else {
            bannerMapper.update(banner);
        }
    }

    public void deleteById(Long id) {
        bannerMapper.deleteById(id);
    }

    public PageResult<Banner> findPage(Banner banner, int pageNum, int pageSize) {
        Long total = bannerMapper.count(banner);
        List<Banner> list = bannerMapper.findList(banner);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<Banner> findAllEnabled() {
        return bannerMapper.findAllEnabled();
    }
}
