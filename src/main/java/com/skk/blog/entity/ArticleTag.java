package com.skk.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("article_tag")
public class ArticleTag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("article_id")
    private Long articleId;

    @TableField("tag_id")
    private Long tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
