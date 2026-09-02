package com.vaccine.common;

import lombok.Data;

import java.util.Arrays;

/**
 * 用户上下文（ThreadLocal存储当前登录用户信息）
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUsername(username);
        userInfo.setRole(role);
        USER_HOLDER.set(userInfo);
    }

    public static UserInfo get() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    public static String getRole() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getRole() : null;
    }

    public static boolean isAdmin() {
        return hasRole("admin");
    }

    public static boolean isCommunityAdmin() {
        return hasRole("community_admin");
    }

    public static boolean isUser() {
        return hasRole("user");
    }

    public static boolean hasRole(String... roles) {
        String currentRole = getRole();
        return currentRole != null && Arrays.stream(roles)
                .anyMatch(role -> role.equalsIgnoreCase(currentRole));
    }

    public static void remove() {
        USER_HOLDER.remove();
    }

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String role;
    }
}
