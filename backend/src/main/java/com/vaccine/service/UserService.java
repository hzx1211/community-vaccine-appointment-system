package com.vaccine.service;

import cn.hutool.crypto.digest.BCrypt;
import com.vaccine.common.PageResult;
import com.vaccine.entity.User;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 普通用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    public void save(User user) {
        if (user.getId() == null) {
            // 新增用户
            // 检查用户名是否存在（同时检查user表和admin表）
            User existUser = userMapper.findByUsername(user.getUsername());
            if (existUser != null) {
                throw BusinessException.of("用户名已存在");
            }
            if (adminMapper.findByUsername(user.getUsername()) != null) {
                throw BusinessException.of("用户名已存在");
            }
            user.setPassword(BCrypt.hashpw("123456")); // 默认密码
            user.setBalance(BigDecimal.ZERO);
            user.setStatus(1);
            userMapper.insert(user);
        } else {
            // 更新用户
            user.setPassword(null); // 不更新密码
            userMapper.update(user);
        }
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public PageResult<User> findPage(User user, int pageNum, int pageSize) {
        Long total = userMapper.count(user);
        List<User> list = userMapper.findList(user);
        // 简单分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = List.of();
        }
        // 清除密码
        list.forEach(u -> u.setPassword(null));
        return PageResult.of(total, list);
    }

    public void updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.update(user);
    }

    public void resetPassword(Long id) {
        userMapper.updatePassword(id, BCrypt.hashpw("123456"));
    }
}
