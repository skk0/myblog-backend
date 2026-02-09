package com.skk.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skk.blog.common.PageResult;
import com.skk.blog.common.Result;
import com.skk.blog.dto.ArticleDTO;
import com.skk.blog.dto.ArticleQueryDTO;
import com.skk.blog.entity.Article;
import com.skk.blog.entity.ArticleTag;
import com.skk.blog.entity.Category;
import com.skk.blog.entity.Tag;
import com.skk.blog.entity.User;
import com.skk.blog.mapper.ArticleTagMapper;
import com.skk.blog.mapper.CategoryMapper;
import com.skk.blog.mapper.TagMapper;
import com.skk.blog.service.ArticleService;
import com.skk.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/articles")
@io.swagger.v3.oas.annotations.tags.Tag(name = "文章管理")
@PreAuthorize("hasRole('ADMIN')")
public class ArticleController {

    private static final String ANONYMOUS_USER = "anonymousUser";

    private final ArticleService articleService;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final UserService userService;

    public ArticleController(ArticleService articleService,
                            CategoryMapper categoryMapper,
                            TagMapper tagMapper,
                            ArticleTagMapper articleTagMapper,
                            UserService userService) {
        this.articleService = articleService;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取文章列表")
    public Result<PageResult<Article>> getArticles(@ModelAttribute ArticleQueryDTO query) {
        List<Article> articles = articleService.getArticleList(
                query.getPage(),
                query.getLimit(),
                query.getKeyword(),
                query.getStatus(),
                query.getCategoryId(),
                query.getTagId()
        );

        Long total = articleService.countArticleList(
                query.getKeyword(),
                query.getStatus(),
                query.getCategoryId(),
                query.getTagId()
        );

        return Result.success(PageResult.of(articles, total, (long) query.getPage(), (long) query.getLimit()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<Article> getArticle(@PathVariable Long id) {
        Article article = articleService.getById(id);
        // 补充分类名称和标签信息
        if (article != null) {
            enrichArticle(article);
        }
        return Result.success(article);
    }

    /**
     * 补充分类和标签信息
     */
    private void enrichArticle(Article article) {
        // 设置分类名称
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }

        // 设置标签
        List<ArticleTag> articleTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>()
                        .eq(ArticleTag::getArticleId, article.getId())
        );

        if (!articleTags.isEmpty()) {
            List<Long> tagIds = articleTags.stream()
                    .map(ArticleTag::getTagId)
                    .collect(Collectors.toList());

            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            List<String> tagNames = tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            article.setTags(tagNames);
        } else {
            article.setTags(Collections.emptyList());
        }
    }

    @PostMapping
    @Operation(summary = "创建文章")
    public Result<Long> createArticle(@Valid @RequestBody ArticleDTO dto) {
        Long userId = getCurrentUserId();
        Long id = articleService.createArticle(dto, userId);
        return Result.success("创建成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文章")
    public Result<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO dto) {
        articleService.updateArticle(id, dto);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布文章")
    public Result<Void> publishArticle(@PathVariable Long id) {
        articleService.updateArticleStatus(id, "published");
        return Result.success("发布成功", null);
    }

    @PutMapping("/{id}/unpublish")
    @Operation(summary = "下架文章")
    public Result<Void> unpublishArticle(@PathVariable Long id) {
        articleService.updateArticleStatus(id, "draft");
        return Result.success("下架成功", null);
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "置顶/取消置顶文章")
    public Result<Void> updateArticleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        articleService.updateArticleTop(id, isTop);
        return Result.success(isTop == 1 ? "置顶成功" : "取消置顶成功", null);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取文章统计")
    public Result<Object> getStats() {
        Object stats = articleService.getArticleStats();
        return Result.success(stats);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("未登录或登录状态已失效");
        }

        String username = authentication.getName();
        if (username == null || ANONYMOUS_USER.equals(username)) {
            throw new RuntimeException("未登录或登录状态已失效");
        }

        User user = userService.getByUsername(username);
        if (user == null || user.getId() == null) {
            throw new RuntimeException("当前用户不存在");
        }
        return user.getId();
    }
}
