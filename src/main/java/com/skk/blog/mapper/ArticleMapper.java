package com.skk.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skk.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<Article> selectRecentArticles(@Param("limit") Integer limit);

    List<Map<String, Object>> selectArchives();

    Long countByStatus(@Param("status") String status);

    Long totalViews();
}
