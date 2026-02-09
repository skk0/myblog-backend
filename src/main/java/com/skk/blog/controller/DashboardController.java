package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.entity.Article;
import com.skk.blog.service.ArticleService;
import com.skk.blog.service.BlogInfoService;
import com.skk.blog.service.CategoryService;
import com.skk.blog.service.CommentService;
import com.skk.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "仪表盘")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private static final String STATUS_ALL = "all";
    private static final String STATUS_PUBLISHED = "published";
    private static final String STATUS_DRAFT = "draft";

    private final ArticleService articleService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final BlogInfoService blogInfoService;

    public DashboardController(ArticleService articleService,
                               CommentService commentService,
                               CategoryService categoryService,
                               TagService tagService,
                               BlogInfoService blogInfoService) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.blogInfoService = blogInfoService;
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "获取仪表盘统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 文章统计
        Map<String, Object> articleStats = new HashMap<>();
        articleStats.put("total", articleService.countArticleList(null, STATUS_ALL, null, null));
        articleStats.put("published", articleService.countArticleList(null, STATUS_PUBLISHED, null, null));
        articleStats.put("drafts", articleService.countArticleList(null, STATUS_DRAFT, null, null));
        Object articleStatsResult = articleService.getArticleStats();
        if (articleStatsResult instanceof Map) {
            Map<String, Object> resultMap = (Map<String, Object>) articleStatsResult;
            articleStats.put("views", resultMap.get("views"));
        } else {
            articleStats.put("views", 0);
        }

        stats.put("articles", articleStats);

        // 评论统计
        Map<String, Object> commentStats = new HashMap<>();
        commentStats.put("total", commentService.count());
        commentStats.put("pending", commentService.getPendingCount());
        stats.put("comments", commentStats);

        // 分类标签统计
        stats.put("categories", categoryService.count());
        stats.put("tags", tagService.count());

        // 最近文章（取最近创建的10篇文章）
        List<Article> recentArticles = articleService.getArticleList(1, 10, null, STATUS_ALL, null, null);
        stats.put("recentArticles", recentArticles);

        // 博客信息
        stats.put("blogInfo", blogInfoService.getBlogInfo());

        return Result.success(stats);
    }
}
