package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 验证密码
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 加密密码
     */
    String encodePassword(String password);
}
