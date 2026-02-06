package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.entity.Article;
import com.skk.blog.entity.Category;
import com.skk.blog.service.ArticleService;
import com.skk.blog.service.BlogInfoService;
import com.skk.blog.service.CategoryService;
import com.skk.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blog")
@Tag(name = "前台博客接口")
public class BlogController {

    private final BlogInfoService blogInfoService;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public BlogController(BlogInfoService blogInfoService,
                          ArticleService articleService,
                          CategoryService categoryService,
                          TagService tagService) {
        this.blogInfoService = blogInfoService;
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/info")
    @Operation(summary = "获取博客信息")
    public Result<Map<String, Object>> getBlogInfo() {
        Map<String, Object> info = blogInfoService.getBlogInfo();
        return Result.success(info);
    }

    @GetMapping("/articles")
    @Operation(summary = "获取文章列表")
    public Result<List<Article>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {

        Long categoryId = null;
        Long tagId = null;

        if (category != null) {
            Category cat = categoryService.getCategoryBySlug(category);
            if (cat != null) {
                categoryId = cat.getId();
            }
        }

        if (tag != null) {
            com.skk.blog.entity.Tag t = tagService.getTagBySlug(tag);
            if (t != null) {
                tagId = t.getId();
            }
        }

        List<Article> articles = articleService.getArticleList(page, limit, keyword, "published", categoryId, tagId);
        return Result.success(articles);
    }

    @GetMapping("/articles/{slug}")
    @Operation(summary = "获取文章详情")
    public Result<Article> getArticle(@PathVariable String slug) {
        Article article = articleService.getArticleDetail(slug);
        if (article == null) {
            return Result.error(404, "文章不存在");
        }
        return Result.success(article);
    }

    @GetMapping("/articles/recent")
    @Operation(summary = "获取近期文章")
    public Result<List<Article>> getRecentArticles(@RequestParam(defaultValue = "5") Integer limit) {
        List<Article> articles = articleService.getRecentArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/categories")
    @Operation(summary = "获取所有分类")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getCategoriesWithCount();
        return Result.success(categories);
    }

    @GetMapping("/tags")
    @Operation(summary = "获取所有标签")
    public Result<List<com.skk.blog.entity.Tag>> getTags() {
        List<com.skk.blog.entity.Tag> tags = tagService.getTagsWithCount();
        return Result.success(tags);
    }

    @GetMapping("/archives")
    @Operation(summary = "获取文章归档")
    public Result<List<Object>> getArchives() {
        List<Object> archives = articleService.getArchives();
        return Result.success(archives);
    }
}
