package com.vaccine.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * 敏感数据加密工具类
 * 用于加密存储用户敏感信息（如身份证号等）
 */
@Component
public class SensitiveDataUtils {

    @Value("${sensitive.data.key:vaccine-system-2024}")
    private String secretKey;

    private AES aes;

    @PostConstruct
    public void init() {
        // 使用MD5将密钥转换为16字节
        byte[] key = SecureUtil.md5().digest(secretKey.getBytes(StandardCharsets.UTF_8));
        this.aes = SecureUtil.aes(key);
    }

    /**
     * 加密敏感数据
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        try {
            return aes.encryptHex(data);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * 解密敏感数据
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        try {
            return aes.decryptStr(encryptedData);
        } catch (Exception e) {
            return encryptedData;
        }
    }

    /**
     * 身份证号脱敏显示
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 手机号脱敏显示
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 邮箱脱敏显示
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return email;
        }
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    /**
     * 真实姓名脱敏显示
     */
    public static String maskRealName(String realName) {
        if (realName == null || realName.length() < 2) {
            return realName;
        }
        if (realName.length() == 2) {
            return realName.charAt(0) + "*";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(realName.charAt(0));
        for (int i = 1; i < realName.length() - 1; i++) {
            sb.append("*");
        }
        sb.append(realName.charAt(realName.length() - 1));
        return sb.toString();
    }
}
