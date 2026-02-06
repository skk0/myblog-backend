package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.entity.User;
import com.skk.blog.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 调试用的测试接口
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public TestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 测试密码匹配
     */
    @GetMapping("/password/{rawPassword}")
    public Result<String> testPassword(@PathVariable String rawPassword) {
        User user = userService.getByUsername("admin");
        if (user == null) {
            return Result.error("用户不存在");
        }

        String encodedPassword = user.getPassword();
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        return Result.success("数据库密码: " + encodedPassword + "\n输入密码: " + rawPassword + "\n匹配结果: " + matches);
    }

    /**
     * 重置密码为指定值
     */
    @GetMapping("/reset-password/{newPassword}")
    public Result<String> resetPassword(@PathVariable String newPassword) {
        User user = userService.getByUsername("admin");
        if (user == null) {
            return Result.error("用户不存在");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userService.updateById(user);

        return Result.success("密码已重置为: " + newPassword + "\n新密码Hash: " + encodedPassword);
    }
}
