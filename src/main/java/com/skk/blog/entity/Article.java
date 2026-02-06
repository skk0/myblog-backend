package com.skk.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("article")
public class Article implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String slug;

    private String excerpt;

    private String content;

    private String cover;

    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;

    private String status;

    private Integer views;

    private Integer likes;

    @TableField("is_top")
    private Integer isTop;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField("publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private Integer commentCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
