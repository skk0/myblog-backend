package com.skk.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("category")
public class Category implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String slug;

    private String icon;

    private String description;

    private String color;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private Integer count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
