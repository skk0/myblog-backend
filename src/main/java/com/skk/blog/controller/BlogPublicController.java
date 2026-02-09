package com.skk.blog.controller;

import com.skk.blog.common.Result;
import com.skk.blog.dto.CommentDTO;
import com.skk.blog.dto.CommentQueryDTO;
import com.skk.blog.entity.Comment;
import com.skk.blog.service.ArticleService;
import com.skk.blog.service.BlogInfoService;
import com.skk.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/blog")
@Tag(name = "前台博客接口")
public class BlogPublicController {

    private static final String STATUS_APPROVED = "approved";

    private final CommentService commentService;
    private final ArticleService articleService;
    private final BlogInfoService blogInfoService;
    private final String uploadPath;

    public BlogPublicController(CommentService commentService,
                                ArticleService articleService,
                                BlogInfoService blogInfoService,
                                @Value("${blog.upload.path}") String uploadPath) {
        this.commentService = commentService;
        this.articleService = articleService;
        this.blogInfoService = blogInfoService;
        this.uploadPath = uploadPath;
    }

    // ===== 评论接口 =====

    @GetMapping("/comments")
    @Operation(summary = "获取评论列表")
    public Result<List<Comment>> getComments(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        CommentQueryDTO query = new CommentQueryDTO();
        query.setPage(page);
        query.setLimit(limit);
        query.setType(type);
        query.setStatus(STATUS_APPROVED); // 前台只显示已审核的评论

        if (StringUtils.hasText(articleId)) {
            try {
                query.setArticleId(Long.parseLong(articleId));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("articleId 参数格式错误");
            }
        }

        List<Comment> comments = commentService.getCommentList(query);
        return Result.success(comments);
    }

    @PostMapping("/comments")
    @Operation(summary = "提交评论")
    public Result<Long> submitComment(@Valid @RequestBody CommentDTO dto) {
        Long id = commentService.submitComment(dto);
        return Result.success("评论提交成功，请在审核后显示", id);
    }

    @PostMapping("/comments/{id}/like")
    @Operation(summary = "点赞评论")
    public Result<Void> likeComment(@PathVariable Long id) {
        commentService.likeComment(id);
        return Result.success("点赞成功", null);
    }

    // ===== 图片上传接口 =====

    @PostMapping("/upload")
    @Operation(summary = "图片上传")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
                return Result.error("文件名非法");
            }
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成新文件名: 时间戳_随机数.扩展名
            String newFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

            // 按日期创建子目录
            String datePath = java.time.LocalDate.now().toString().replace("-", "/");
            Path uploadDir = Paths.get(uploadPath, datePath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问URL
            String url = "/uploads/" + datePath + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", newFilename);

            return Result.success(result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    // ===== 表情包接口 =====

    @GetMapping("/emojis")
    @Operation(summary = "获取表情包列表")
    public Result<List<Map<String, String>>> getEmojis() {
        List<Map<String, String>> emojis = new ArrayList<>();

        // 常用表情
        String[] emojiList = {"😀", "😁", "😂", "🤣", "😃", "😄", "😅", "😆", "😉", "😊",
                "😋", "😎", "😍", "😘", "🥰", "😗", "😙", "😚", "🙂", "🤗",
                "🤩", "🤔", "🤨", "😐", "😑", "😶", "🙄", "😏", "😣", "😥",
                "😮", "🤐", "😯", "😪", "😫", "😴", "😌", "😛", "😜", "😝",
                "🤤", "😒", "😓", "😔", "😕", "🙃", "🤑", "😲", "☹️", "🙁",
                "😖", "😞", "😟", "😤", "😢", "😭", "😦", "😧", "😨", "😩",
                "🤯", "😬", "😰", "😱", "🥵", "🥶", "😮", "🤬", "😷", "🤒",
                "🤕", "🤢", "🤮", "🤧", "😵", "🤯", "🤠", "🥳", "😎", "🤓",
                "🧐", "😕", "😟", "🙁", "☹️", "😧", "😨", "😰", "😥", "😢"};

        for (String emoji : emojiList) {
            Map<String, String> map = new HashMap<>();
            map.put("emoji", emoji);
            map.put("url", "/emojis/" + emoji + ".png"); // 可以配置实际的表情包图片
            emojis.add(map);
        }

        return Result.success(emojis);
    }
}
