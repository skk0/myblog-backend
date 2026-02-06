package com.skk.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    private String type;

    private Long articleId;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;

    private String website;

    private String avatar;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private String images;

    private Long parentId;
}
