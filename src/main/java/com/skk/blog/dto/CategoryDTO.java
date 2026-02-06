package com.skk.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String slug;

    private String icon;

    private String description;

    private String color;

    private Integer sortOrder;
}
