package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.service.BlogInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/settings")
@Tag(name = "系统设置")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {

    private final BlogInfoService blogInfoService;

    public SettingsController(BlogInfoService blogInfoService) {
        this.blogInfoService = blogInfoService;
    }

    @GetMapping
    @Operation(summary = "获取系统设置")
    public Result<Map<String, Object>> getSettings() {
        Map<String, Object> settings = blogInfoService.getBlogInfo();
        return Result.success(settings);
    }

    @PutMapping
    @Operation(summary = "更新系统设置")
    public Result<Void> updateSettings(@RequestBody Map<String, String> settings) {
        blogInfoService.updateBlogInfo(settings);
        return Result.success("设置更新成功", null);
    }
}
