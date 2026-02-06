package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.dto.TagDTO;
import com.skk.blog.entity.Tag;
import com.skk.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理")
@PreAuthorize("hasRole('ADMIN')")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @Operation(summary = "获取标签列表")
    public Result<List<Tag>> getTags() {
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    @GetMapping("/with-count")
    @Operation(summary = "获取标签列表(含文章数)")
    public Result<List<Tag>> getTagsWithCount() {
        List<Tag> tags = tagService.getTagsWithCount();
        return Result.success(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情")
    public Result<Tag> getTag(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    @PostMapping
    @Operation(summary = "创建标签")
    public Result<Long> createTag(@Valid @RequestBody TagDTO dto) {
        Long id = tagService.createTag(dto);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签")
    public Result<Void> updateTag(@PathVariable Long id, @Valid @RequestBody TagDTO dto) {
        tagService.updateTag(id, dto);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("删除成功", null);
    }
}
