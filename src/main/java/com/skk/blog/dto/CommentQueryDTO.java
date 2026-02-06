package com.skk.blog.dto;

import lombok.Data;

@Data
public class CommentQueryDTO {

    private Integer page = 1;

    private Integer limit = 10;

    private String type;

    private Long articleId;

    private String status;
}
