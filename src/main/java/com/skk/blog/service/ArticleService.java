package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.Article;
import com.skk.blog.dto.ArticleDTO;

import java.util.List;

public interface ArticleService extends IService<Article> {

    /**
     * 获取文章列表
     */
    List<Article> getArticleList(Integer page, Integer limit, String keyword, String status, Long categoryId, Long tagId);

    /**
     * 获取文章详情
     */
    Article getArticleDetail(String slug);

    /**
     * 获取近期文章
     */
    List<Article> getRecentArticles(Integer limit);

    /**
     * 获取文章归档
     */
    List<Object> getArchives();

    /**
     * 创建文章
     */
    Long createArticle(ArticleDTO dto, Long userId);

    /**
     * 更新文章
     */
    void updateArticle(Long id, ArticleDTO dto);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

    /**
     * 发布/下架文章
     */
    void updateArticleStatus(Long id, String status);

    /**
     * 置顶文章
     */
    void updateArticleTop(Long id, Integer isTop);

    /**
     * 增加阅读量
     */
    void incrementViews(Long id);

    /**
     * 获取文章统计
     */
    Object getArticleStats();
}
