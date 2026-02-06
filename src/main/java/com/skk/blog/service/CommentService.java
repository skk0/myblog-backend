package com.skk.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skk.blog.entity.Comment;
import com.skk.blog.dto.CommentDTO;
import com.skk.blog.dto.CommentQueryDTO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 获取评论列表
     */
    List<Comment> getCommentList(CommentQueryDTO query);

    /**
     * 获取文章评论
     */
    List<Comment> getArticleComments(Long articleId);

    /**
     * 获取待审核评论数量
     */
    Long getPendingCount();

    /**
     * 提交评论
     */
    Long submitComment(CommentDTO dto);

    /**
     * 审核通过
     */
    void approveComment(Long id);

    /**
     * 审核拒绝
     */
    void rejectComment(Long id);

    /**
     * 删除评论
     */
    void deleteComment(Long id);

    /**
     * 点赞评论
     */
    void likeComment(Long id);
}
