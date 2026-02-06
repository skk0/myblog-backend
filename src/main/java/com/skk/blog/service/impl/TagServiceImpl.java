package com.skk.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skk.blog.dto.TagDTO;
import com.skk.blog.entity.Tag;
import com.skk.blog.mapper.TagMapper;
import com.skk.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;

    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public List<Tag> getAllTags() {
        return this.lambdaQuery()
                .orderByAsc(Tag::getId)
                .list();
    }

    @Override
    public List<Tag> getTagsWithCount() {
        return tagMapper.selectTagsWithCount();
    }

    @Override
    public Tag getTagById(Long id) {
        return this.getById(id);
    }

    @Override
    public Tag getTagBySlug(String slug) {
        return this.lambdaQuery()
                .eq(Tag::getSlug, slug)
                .one();
    }

    @Override
    @Transactional
    public Long createTag(TagDTO dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tag.setSlug(dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getName()));
        tag.setColor(dto.getColor());

        this.save(tag);
        return tag.getId();
    }

    @Override
    @Transactional
    public void updateTag(Long id, TagDTO dto) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }

        tag.setName(dto.getName());
        if (dto.getSlug() != null) {
            tag.setSlug(dto.getSlug());
        }
        if (dto.getColor() != null) {
            tag.setColor(dto.getColor());
        }

        this.updateById(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        this.removeById(id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
