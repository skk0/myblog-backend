package com.skk.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skk.blog.entity.BlogInfo;
import com.skk.blog.mapper.BlogInfoMapper;
import com.skk.blog.service.BlogInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo> implements BlogInfoService {

    private final BlogInfoMapper blogInfoMapper;

    public BlogInfoServiceImpl(BlogInfoMapper blogInfoMapper) {
        this.blogInfoMapper = blogInfoMapper;
    }

    @Override
    public Map<String, Object> getBlogInfo() {
        List<Map<String, String>> list = blogInfoMapper.selectAllKeyValue();
        Map<String, Object> result = new HashMap<>();

        // 构建社交链接对象
        Map<String, String> social = new HashMap<>();

        for (Map<String, String> item : list) {
            String configKey = item.get("config_key");
            String configValue = item.get("config_value");

            // 将配置key映射为前端期望的字段名
            switch (configKey) {
                case "blog_title":
                    result.put("title", configValue);
                    break;
                case "blog_subtitle":
                    result.put("subtitle", configValue);
                    break;
                case "blog_author":
                    result.put("author", configValue);
                    break;
                case "blog_avatar":
                    result.put("avatar", configValue);
                    break;
                case "blog_description":
                    result.put("description", configValue);
                    break;
                case "blog_location":
                    result.put("location", configValue);
                    break;
                case "contact_email":
                    result.put("email", configValue);
                    break;
                case "social_github":
                    social.put("github", configValue);
                    break;
                case "social_weibo":
                    social.put("weibo", configValue);
                    break;
                case "social_twitter":
                    social.put("twitter", configValue);
                    break;
                default:
                    result.put(configKey, configValue);
            }
        }

        result.put("social", social);

        Object avatar = result.get("avatar");
        if (avatar instanceof String) {
            String avatarValue = ((String) avatar).trim();
            if (avatarValue.isEmpty() || "/".equals(avatarValue)) {
                result.put("avatar", "");
            }
        }

        return result;
    }

    @Override
    public void updateBlogInfo(Map<String, String> info) {
        for (Map.Entry<String, String> entry : info.entrySet()) {
            BlogInfo blogInfo = blogInfoMapper.selectByKey(entry.getKey());
            if (blogInfo != null) {
                blogInfo.setConfigValue(entry.getValue());
                blogInfoMapper.updateById(blogInfo);
            }
        }
    }

    @Override
    public String getValue(String configKey) {
        BlogInfo blogInfo = blogInfoMapper.selectByKey(configKey);
        return blogInfo != null ? blogInfo.getConfigValue() : null;
    }
}
