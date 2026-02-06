package com.skk.blog.dto;

import lombok.Data;

@Data
public class ArticleQueryDTO {

    private Integer page = 1;

    private Integer limit = 10;

    private String keyword;

    private String status;

    private Long categoryId;

    private Long tagId;
}
