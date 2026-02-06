package com.skk.blog.controller;

import com.skk.blog.common.ApiLog;
import com.skk.blog.common.JwtTokenUtil;
import com.skk.blog.common.Result;
import com.skk.blog.dto.LoginDTO;
import com.skk.blog.entity.User;
import com.skk.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @ApiLog(value = "用户登录", recordParams = true, recordResult = false)
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getByUsername(loginDTO.getUsername());
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "avatar", user.getAvatar()==null?"/":user.getAvatar()
        ));

        return Result.success("登录成功", result);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("退出成功", null);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<User> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public Result<Void> updateProfile(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User existUser = userService.getByUsername(username);
        existUser.setNickname(user.getNickname());
        existUser.setAvatar(user.getAvatar());
        existUser.setEmail(user.getEmail());
        existUser.setBio(user.getBio());

        userService.updateById(existUser);
        return Result.success("更新成功", null);
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getByUsername(username);
        if (!userService.verifyPassword(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        user.setPassword(userService.encodePassword(newPassword));
        userService.updateById(user);

        return Result.success("密码修改成功", null);
    }
}
