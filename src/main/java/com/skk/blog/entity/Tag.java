package com.skk.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tag")
public class Tag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String slug;

    private String color;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private Integer count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
