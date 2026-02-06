package com.skk.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ArticleDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String slug;

    private String excerpt;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String cover;

    private Long categoryId;

    private List<Long> tagIds;

    private String status;

    private Integer isTop;
}
