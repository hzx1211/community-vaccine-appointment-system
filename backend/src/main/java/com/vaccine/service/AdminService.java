package com.vaccine.service;

import cn.hutool.crypto.digest.BCrypt;
import com.vaccine.common.PageResult;
import com.vaccine.entity.Admin;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员服务
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    public Admin findById(Long id) {
        Admin admin = adminMapper.findById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return admin;
    }

    public void save(Admin admin) {
        if (admin.getId() == null) {
            // 新增管理员
            // 检查用户名是否存在（同时检查admin表和user表）
            Admin existAdmin = adminMapper.findByUsername(admin.getUsername());
            if (existAdmin != null) {
                throw BusinessException.of("用户名已存在");
            }
            if (userMapper.findByUsername(admin.getUsername()) != null) {
                throw BusinessException.of("用户名已存在");
            }
            admin.setPassword(BCrypt.hashpw("123456")); // 默认密码
            admin.setStatus(1);
            adminMapper.insert(admin);
        } else {
            // 更新管理员
            admin.setPassword(null); // 不更新密码
            adminMapper.update(admin);
        }
    }

    public void deleteById(Long id) {
        adminMapper.deleteById(id);
    }

    public PageResult<Admin> findPage(Admin admin, int pageNum, int pageSize) {
        Long total = adminMapper.count(admin);
        List<Admin> list = adminMapper.findList(admin);
        // 简单分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        // 清除密码
        list.forEach(a -> a.setPassword(null));
        return PageResult.of(total, list);
    }

    public void updateStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        adminMapper.update(admin);
    }

    public void resetPassword(Long id) {
        adminMapper.updatePassword(id, BCrypt.hashpw("123456"));
    }
}
