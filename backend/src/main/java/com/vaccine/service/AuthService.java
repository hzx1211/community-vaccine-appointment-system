package com.vaccine.service;

import cn.hutool.crypto.digest.BCrypt;
import com.vaccine.common.UserContext;
import com.vaccine.entity.Admin;
import com.vaccine.entity.User;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AdminMapper;
import com.vaccine.mapper.UserMapper;
import com.vaccine.utils.JwtUtils;
import com.vaccine.utils.SensitiveDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private SensitiveDataUtils sensitiveDataUtils;

    /**
     * 用户注册（带限流）
     */
    public void register(String username, String password, String realName, String phone, String clientIp) {
        // 注册限流检查
        if (!rateLimitService.isRegisterAllowed(clientIp)) {
            throw BusinessException.of("注册过于频繁，请稍后重试");
        }

        // 检查用户名是否存在（同时检查user表和admin表）
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw BusinessException.of("用户名已存在");
        }
        Admin existAdmin = adminMapper.findByUsername(username);
        if (existAdmin != null) {
            throw BusinessException.of("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);
        userMapper.insert(user);
    }

    /**
     * 用户注册（无限流，兼容旧接口）
     */
    public void register(String username, String password, String realName, String phone) {
        register(username, password, realName, phone, "unknown");
    }

    /**
     * 普通用户登录（带限流，Token存储到Redis）
     */
    public Map<String, Object> loginUser(String username, String password, String clientIp) {
        // 登录限流检查 系统会做登录限流，防止同一ip在短时间内尝试登录
        if (!rateLimitService.isLoginAllowed(clientIp)) {
            throw BusinessException.of("登录尝试过于频繁，请稍后重试");
        }
        //登录并不是一进来就查数据库，而是先做一层安全控制

        //
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用");
        }

        // 使用TokenService生成Token并存储到Redis
        String token = tokenService.createToken(user.getId(), user.getUsername(), "user");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", "user");
        result.put("avatar", user.getAvatar());
        result.put("balance", user.getBalance());
        return result;
    }

    /**
     * 管理员登录（带限流，Token存储到Redis）
     */
    public Map<String, Object> loginAdmin(String username, String password, String clientIp) {
        // 登录限流检查
        if (!rateLimitService.isLoginAllowed(clientIp)) {
            throw BusinessException.of("登录尝试过于频繁，请稍后重试");
        }

        Admin admin = adminMapper.findByUsername(username);
        if (admin == null) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, admin.getPassword())) {
            throw BusinessException.of("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw BusinessException.of("账号已被禁用");
        }

        // 使用TokenService生成Token并存储到Redis
        String token = tokenService.createToken(admin.getId(), admin.getUsername(), admin.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("realName", admin.getRealName());
        result.put("role", admin.getRole());
        result.put("avatar", admin.getAvatar());
        result.put("communityId", admin.getCommunityId());
        return result;
    }

    /**
     * 用户登录（带限流，Token存储到Redis）- 兼容旧接口，自动判断用户类型
     */
    public Map<String, Object> login(String username, String password, String clientIp) {
        // 登录限流检查
        if (!rateLimitService.isLoginAllowed(clientIp)) {
            throw BusinessException.of("登录尝试过于频繁，请稍后重试");
        }

        // 先尝试从user表查找
        User user = userMapper.findByUsername(username);
        if (user != null) {
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw BusinessException.of("用户名或密码错误");
            }
            if (user.getStatus() == 0) {
                throw BusinessException.of("账号已被禁用");
            }

            String token = tokenService.createToken(user.getId(), user.getUsername(), "user");
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("role", "user");
            result.put("avatar", user.getAvatar());
            result.put("balance", user.getBalance());
            return result;
        }

        // 再尝试从admin表查找
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null) {
            if (!BCrypt.checkpw(password, admin.getPassword())) {
                throw BusinessException.of("用户名或密码错误");
            }
            if (admin.getStatus() == 0) {
                throw BusinessException.of("账号已被禁用");
            }

            String token = tokenService.createToken(admin.getId(), admin.getUsername(), admin.getRole());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", admin.getId());
            result.put("username", admin.getUsername());
            result.put("realName", admin.getRealName());
            result.put("role", admin.getRole());
            result.put("avatar", admin.getAvatar());
            result.put("communityId", admin.getCommunityId());
            return result;
        }

        throw BusinessException.of("用户名或密码错误");
    }

    /**
     * 用户登录（无限流，兼容旧接口）
     */
    public Map<String, Object> login(String username, String password) {
        return login(username, password, "unknown");
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenService.removeToken(token);
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenService.refreshToken(token);
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser() {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        if (userId == null) {
            throw BusinessException.of(401, "未登录");
        }
        
        // 如果是管理员，返回null（管理员应该使用getCurrentAdmin）
        if (!"user".equals(role)) {
            return null;
        }
        
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }

    /**
     * 获取当前管理员信息
     */
    public Admin getCurrentAdmin() {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        if (userId == null) {
            throw BusinessException.of(401, "未登录");
        }
        
        // 如果是普通用户，返回null
        if ("user".equals(role)) {
            return null;
        }
        
        Admin admin = adminMapper.findById(userId);
        if (admin != null) {
            admin.setPassword(null); // 不返回密码
        }
        return admin;
    }

    /**
     * 修改密码
     */
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        
        if ("user".equals(role)) {
            User user = userMapper.findById(userId);
            if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
                throw BusinessException.of("原密码错误");
            }
            userMapper.updatePassword(userId, BCrypt.hashpw(newPassword));
        } else {
            Admin admin = adminMapper.findById(userId);
            if (!BCrypt.checkpw(oldPassword, admin.getPassword())) {
                throw BusinessException.of("原密码错误");
            }
            adminMapper.updatePassword(userId, BCrypt.hashpw(newPassword));
        }
        
        // 修改密码后强制登出
        tokenService.removeTokenByUserId(userId);
    }

    /**
     * 更新个人信息
     */
    public void updateProfile(User user) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        
        if (!"user".equals(role)) {
            throw BusinessException.of("管理员请使用管理员接口更新信息");
        }
        
        user.setId(userId);
        user.setPassword(null); // 不允许通过此接口修改密码
        user.setBalance(null);  // 不允许修改余额
        
        // 加密身份证号
        if (user.getIdCard() != null && !user.getIdCard().isEmpty()) {
            user.setIdCard(sensitiveDataUtils.encrypt(user.getIdCard()));
        }
        
        userMapper.update(user);
    }

    /**
     * 更新管理员个人信息
     */
    public void updateAdminProfile(Admin admin) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        
        if ("user".equals(role)) {
            throw BusinessException.of("普通用户请使用用户接口更新信息");
        }
        
        admin.setId(userId);
        admin.setPassword(null); // 不允许通过此接口修改密码
        admin.setRole(null);     // 不允许修改角色
        admin.setCommunityId(null); // 不允许修改所属社区
        
        adminMapper.update(admin);
    }

    /**
     * 获取当前用户信息（解密敏感数据）
     */
    public User getCurrentUserWithDecrypt() {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        if (userId == null) {
            throw BusinessException.of(401, "未登录");
        }
        
        // 如果是管理员，返回null
        if (!"user".equals(role)) {
            return null;
        }
        
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(null);
            // 解密身份证号
            if (user.getIdCard() != null && !user.getIdCard().isEmpty()) {
                user.setIdCard(sensitiveDataUtils.decrypt(user.getIdCard()));
            }
        }
        return user;
    }

    /**
     * 获取用户信息（脱敏显示）
     */
    public User getUserMasked(Long userId) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(null);
            user.setIdCard(SensitiveDataUtils.maskIdCard(sensitiveDataUtils.decrypt(user.getIdCard())));
            user.setPhone(SensitiveDataUtils.maskPhone(user.getPhone()));
            user.setEmail(SensitiveDataUtils.maskEmail(user.getEmail()));
        }
        return user;
    }
}
