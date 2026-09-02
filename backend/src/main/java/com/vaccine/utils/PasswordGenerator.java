package com.vaccine.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码生成工具（运行main方法生成密码哈希）
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        String password = "123456";
        String hash = BCrypt.hashpw(password);
        System.out.println("密码: " + password);
        System.out.println("哈希: " + hash);
        System.out.println("验证: " + BCrypt.checkpw(password, hash));
    }
}
