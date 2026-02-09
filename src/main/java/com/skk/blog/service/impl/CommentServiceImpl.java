package com.skk.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skk.blog.dto.CommentDTO;
import com.skk.blog.dto.CommentQueryDTO;
import com.skk.blog.entity.Article;
import com.skk.blog.entity.Comment;
import com.skk.blog.mapper.ArticleMapper;
import com.skk.blog.mapper.CommentMapper;
import com.skk.blog.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    public CommentServiceImpl(CommentMapper commentMapper, ArticleMapper articleMapper) {
        this.commentMapper = commentMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public List<Comment> getCommentList(CommentQueryDTO query) {
        Page<Comment> pageParam = new Page<>(query.getPage(), query.getLimit());

        LambdaQueryWrapper<Comment> wrapper = buildCommentListWrapper(query);

        IPage<Comment> commentPage = this.page(pageParam, wrapper);

        // 填充文章信息
        commentPage.getRecords().forEach(this::enrichComment);

        return commentPage.getRecords();
    }

    @Override
    public Long countCommentList(CommentQueryDTO query) {
        return this.count(buildCommentListWrapper(query));
    }

    @Override
    public List<Comment> getArticleComments(Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId)
               .eq(Comment::getApproved, 1)
               .eq(Comment::getParentId, 0)
               .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = this.list(wrapper);

        // 获取回复
        comments.forEach(comment -> {
            List<Comment> replies = this.lambdaQuery()
                    .eq(Comment::getParentId, comment.getId())
                    .eq(Comment::getApproved, 1)
                    .orderByAsc(Comment::getCreateTime)
                    .list();
            comment.setReplies(replies);
        });

        return comments;
    }

    @Override
    public Long getPendingCount() {
        return commentMapper.pendingCount();
    }

    @Override
    @Transactional
    public Long submitComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setType(dto.getType() != null ? dto.getType() : "article");
        comment.setArticleId(dto.getArticleId());
        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setWebsite(dto.getWebsite());
        comment.setAvatar(dto.getAvatar());
        comment.setContent(dto.getContent());
        comment.setImages(dto.getImages());
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setApproved(0); // 默认待审核
        comment.setLikes(0);

        this.save(comment);
        return comment.getId();
    }

    @Override
    @Transactional
    public void approveComment(Long id) {
        Comment comment = this.getById(id);
        if (comment != null) {
            comment.setApproved(1);
            this.updateById(comment);
        }
    }

    @Override
    @Transactional
    public void rejectComment(Long id) {
        Comment comment = this.getById(id);
        if (comment != null) {
            comment.setApproved(2);
            this.updateById(comment);
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        // 同时删除子评论
        List<Comment> replies = this.lambdaQuery()
                .eq(Comment::getParentId, id)
                .list();

        if (!replies.isEmpty()) {
            List<Long> ids = replies.stream().map(Comment::getId).collect(Collectors.toList());
            this.removeByIds(ids);
        }

        this.removeById(id);
    }

    @Override
    public void likeComment(Long id) {
        this.lambdaUpdate()
                .eq(Comment::getId, id)
                .setSql("likes = likes + 1")
                .update();
    }

    private void enrichComment(Comment comment) {
        if (comment.getArticleId() != null) {
            Article article = articleMapper.selectById(comment.getArticleId());
            if (article != null) {
                comment.setArticleSlug(article.getSlug());
                comment.setArticleTitle(article.getTitle());
            }
        }
    }

    private LambdaQueryWrapper<Comment> buildCommentListWrapper(CommentQueryDTO query) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();

        if (query.getType() != null) {
            wrapper.eq(Comment::getType, query.getType());
        }

        if (query.getArticleId() != null) {
            wrapper.eq(Comment::getArticleId, query.getArticleId());
        }

        if (STATUS_PENDING.equals(query.getStatus())) {
            wrapper.eq(Comment::getApproved, 0);
        } else if (STATUS_APPROVED.equals(query.getStatus())) {
            wrapper.eq(Comment::getApproved, 1);
        } else if (STATUS_REJECTED.equals(query.getStatus())) {
            wrapper.eq(Comment::getApproved, 2);
        }

        wrapper.orderByDesc(Comment::getCreateTime);
        return wrapper;
    }
}
