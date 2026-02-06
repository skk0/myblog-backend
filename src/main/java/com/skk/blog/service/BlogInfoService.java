package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.BlogInfo;

import java.util.Map;

public interface BlogInfoService extends IService<BlogInfo> {

    /**
     * 获取博客信息
     */
    Map<String, Object> getBlogInfo();

    /**
     * 更新博客信息
     */
    void updateBlogInfo(Map<String, String> info);

    /**
     * 根据key获取value
     */
    String getValue(String key);
}
