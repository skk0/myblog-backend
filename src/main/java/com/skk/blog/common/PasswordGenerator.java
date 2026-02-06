package com.skk.blog.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("密码长度: " + encodedPassword.length());

        // 验证密码
        System.out.println("验证结果: " + encoder.matches(rawPassword, encodedPassword));
    }
}
