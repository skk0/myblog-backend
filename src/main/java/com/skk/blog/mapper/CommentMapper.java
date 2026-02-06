package com.skk.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skk.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    Comment selectCommentWithArticle(@Param("id") Long id);

    Long countByArticleId(@Param("articleId") Long articleId);

    Long pendingCount();
}
