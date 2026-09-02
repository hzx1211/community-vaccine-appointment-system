package com.vaccine.service;

import com.vaccine.common.PageResult;
import com.vaccine.entity.Community;
import com.vaccine.mapper.AppointmentMapper;
import com.vaccine.mapper.CommunityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区服务
 */
@Service
public class CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    public Community findById(Long id) {
        return communityMapper.findById(id);
    }

    public void save(Community community) {
        if (community.getId() == null) {
            community.setStatus(1);
            communityMapper.insert(community);
        } else {
            communityMapper.update(community);
        }
    }

    public void deleteById(Long id) {
        communityMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            communityMapper.deleteBatch(ids);
        }
    }

    public PageResult<Community> findPage(Community community, int pageNum, int pageSize) {
        Long total = communityMapper.count(community);
        List<Community> list = communityMapper.findList(community);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        return PageResult.of(total, list);
    }

    public List<Community> findAllEnabled() {
        return communityMapper.findAllEnabled();
    }

    public void updateStatus(Long id, Integer status) {
        Community community = new Community();
        community.setId(id);
        community.setStatus(status);
        communityMapper.update(community);
    }

    public Map<String, Object> getCommunityStats(Long communityId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAppointments", appointmentMapper.countByCommunityId(communityId));
        stats.put("pendingAppointments", appointmentMapper.countByCommunityIdAndStatus(communityId, 0));
        stats.put("completedAppointments", appointmentMapper.countByCommunityIdAndStatus(communityId, 4));
        return stats;
    }
}
