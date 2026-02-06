package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.Tag;
import com.skk.blog.dto.TagDTO;

import java.util.List;

public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签
     */
    List<Tag> getAllTags();

    /**
     * 获取标签列表(含文章数)
     */
    List<Tag> getTagsWithCount();

    /**
     * 根据ID获取标签
     */
    Tag getTagById(Long id);

    /**
     * 根据slug获取标签
     */
    Tag getTagBySlug(String slug);

    /**
     * 创建标签
     */
    Long createTag(TagDTO dto);

    /**
     * 更新标签
     */
    void updateTag(Long id, TagDTO dto);

    /**
     * 删除标签
     */
    void deleteTag(Long id);
}
