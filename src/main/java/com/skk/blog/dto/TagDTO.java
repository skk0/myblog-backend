package com.skk.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagDTO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String slug;

    private String color;
}
