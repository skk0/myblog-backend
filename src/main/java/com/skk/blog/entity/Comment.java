package com.skk.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("comment")
public class Comment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    @TableField("article_id")
    private Long articleId;

    @TableField(exist = false)
    private String articleSlug;

    @TableField(exist = false)
    private String articleTitle;

    private String nickname;

    private String email;

    private String website;

    private String avatar;

    private String content;

    private String images;

    private Integer likes;

    @TableField("parent_id")
    private Long parentId;

    private Integer approved;

    private String ip;

    private String userAgent;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private List<Comment> replies;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
