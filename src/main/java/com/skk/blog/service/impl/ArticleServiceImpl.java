package com.skk.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skk.blog.dto.ArticleDTO;
import com.skk.blog.entity.Article;
import com.skk.blog.entity.ArticleTag;
import com.skk.blog.entity.Tag;
import com.skk.blog.mapper.ArticleMapper;
import com.skk.blog.mapper.ArticleTagMapper;
import com.skk.blog.mapper.TagMapper;
import com.skk.blog.service.ArticleService;
import com.skk.blog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final String STATUS_ALL = "all";
    private static final String STATUS_PUBLISHED = "published";
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_ARCHIVED = "archived";

    private final CategoryService categoryService;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public ArticleServiceImpl(CategoryService categoryService,
                              TagMapper tagMapper,
                              ArticleTagMapper articleTagMapper) {
        this.categoryService = categoryService;
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    @Override
    public List<Article> getArticleList(Integer page, Integer limit, String keyword, String status, Long categoryId, Long tagId) {
        Page<Article> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<Article> wrapper = buildArticleListWrapper(keyword, status, categoryId, tagId);
        if (wrapper == null) {
            return Collections.emptyList();
        }

        IPage<Article> articlePage = this.page(pageParam, wrapper);
        List<Article> articles = articlePage.getRecords();

        // 设置分类名称和标签
        articles.forEach(this::enrichArticle);

        return articles;
    }

    @Override
    public Long countArticleList(String keyword, String status, Long categoryId, Long tagId) {
        LambdaQueryWrapper<Article> wrapper = buildArticleListWrapper(keyword, status, categoryId, tagId);
        if (wrapper == null) {
            return 0L;
        }
        return this.count(wrapper);
    }

    @Override
    public Article getArticleDetail(String slug) {
        Article article = this.lambdaQuery()
                .eq(Article::getSlug, slug)
                .eq(Article::getStatus, STATUS_PUBLISHED)
                .one();

        if (article != null) {
            enrichArticle(article);
            // 增加阅读量
            incrementViews(article.getId());
            article.setViews(article.getViews() + 1);
        }

        return article;
    }

    @Override
    public List<Article> getRecentArticles(Integer limit) {
        List<Article> articles = this.baseMapper.selectRecentArticles(limit);
        articles.forEach(this::enrichArticle);
        return articles;
    }

    @Override
    public List<Object> getArchives() {
        List<Map<String, Object>> archives = this.baseMapper.selectArchives();
        return new ArrayList<>(archives);
    }

    @Override
    @Transactional
    public Long createArticle(ArticleDTO dto, Long userId) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSlug(generateSlug(dto.getTitle()));
        article.setExcerpt(dto.getExcerpt());
        article.setContent(dto.getContent());
        article.setCover(dto.getCover());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : STATUS_DRAFT);
        article.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        article.setViews(0);
        article.setLikes(0);

        if (STATUS_PUBLISHED.equals(article.getStatus())) {
            article.setPublishTime(new Date());
        }

        this.save(article);

        // 保存标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), dto.getTagIds());
        }

        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleDTO dto) {
        Article article = this.getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        article.setTitle(dto.getTitle());
        article.setSlug(generateSlug(dto.getTitle()));
        article.setExcerpt(dto.getExcerpt());
        article.setContent(dto.getContent());
        article.setCover(dto.getCover());
        article.setCategoryId(dto.getCategoryId());
        article.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : article.getIsTop());

        String oldStatus = article.getStatus();
        String newStatus = dto.getStatus() != null ? dto.getStatus() : oldStatus;
        article.setStatus(newStatus);

        // 如果从草稿变为发布，设置发布时间
        if (STATUS_DRAFT.equals(oldStatus) && STATUS_PUBLISHED.equals(newStatus)) {
            article.setPublishTime(new Date());
        }

        this.updateById(article);

        // 更新标签关联
        articleTagMapper.deleteByArticleId(id);
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveArticleTags(id, dto.getTagIds());
        }
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        this.removeById(id);
        articleTagMapper.deleteByArticleId(id);
    }

    @Override
    public void updateArticleStatus(Long id, String status) {
        Article article = this.getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        article.setStatus(status);
        if (STATUS_PUBLISHED.equals(status)) {
            article.setPublishTime(new Date());
        }
        this.updateById(article);
    }

    @Override
    public void updateArticleTop(Long id, Integer isTop) {
        Article article = this.getById(id);
        if (article != null) {
            article.setIsTop(isTop);
            this.updateById(article);
        }
    }

    @Override
    public void incrementViews(Long id) {
        this.lambdaUpdate()
                .eq(Article::getId, id)
                .setSql("views = views + 1")
                .update();
    }

    @Override
    public Object getArticleStats() {
        Map<String, Object> stats = new HashMap<>();
        Long totalViews = this.baseMapper.totalViews();
        stats.put("total", this.count());
        stats.put("published", this.baseMapper.countByStatus(STATUS_PUBLISHED));
        stats.put("drafts", this.baseMapper.countByStatus(STATUS_DRAFT));
        stats.put("archived", this.baseMapper.countByStatus(STATUS_ARCHIVED));
        stats.put("views", totalViews != null ? totalViews : 0L);
        return stats;
    }

    private LambdaQueryWrapper<Article> buildArticleListWrapper(String keyword, String status, Long categoryId, Long tagId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(status) && !STATUS_ALL.equals(status)) {
            wrapper.eq(Article::getStatus, status);
        }

        wrapper.orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreateTime);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword));
        }

        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }

        if (tagId != null) {
            List<Long> articleIds = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>()
                            .eq(ArticleTag::getTagId, tagId)
            ).stream().map(ArticleTag::getArticleId).collect(Collectors.toList());

            if (articleIds.isEmpty()) {
                return null;
            }
            wrapper.in(Article::getId, articleIds);
        }

        return wrapper;
    }

    private void enrichArticle(Article article) {
        // 设置分类名称
        if (article.getCategoryId() != null) {
            var category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }

        // 设置标签 - 返回字符串数组供前端使用
        List<ArticleTag> articleTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>()
                        .eq(ArticleTag::getArticleId, article.getId())
        );

        if (!articleTags.isEmpty()) {
            List<Long> tagIds = articleTags.stream()
                    .map(ArticleTag::getTagId)
                    .collect(Collectors.toList());

            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            // 返回标签名字符串数组
            List<String> tagNames = tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            article.setTags(tagNames);  // 存储为字符串数组
        } else {
            article.setTags(Collections.emptyList());
        }
    }

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }

    private String generateSlug(String title) {
        // 简单生成slug: 将标题转为拼音或拼音首字母
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // 如果slug已存在，添加随机后缀
        long count = this.lambdaQuery()
                .like(Article::getSlug, slug + "%")
                .count();

        if (count > 0) {
            slug = slug + "-" + (count + 1);
        }

        return slug;
    }
}
