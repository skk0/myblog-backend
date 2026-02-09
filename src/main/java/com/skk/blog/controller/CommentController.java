package com.skk.blog.controller;

import com.skk.blog.common.PageResult;
import com.skk.blog.common.Result;
import com.skk.blog.dto.CommentQueryDTO;
import com.skk.blog.entity.Comment;
import com.skk.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@Tag(name = "评论管理")
@PreAuthorize("hasRole('ADMIN')")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "获取评论列表")
    public Result<PageResult<Comment>> getComments(@ModelAttribute CommentQueryDTO query) {
        List<Comment> comments = commentService.getCommentList(query);
        Long total = commentService.countCommentList(query);

        return Result.success(PageResult.of(comments, total, (long) query.getPage(), (long) query.getLimit()));
    }

    @GetMapping("/pending-count")
    @Operation(summary = "获取待审核数量")
    public Result<Long> getPendingCount() {
        Long count = commentService.getPendingCount();
        return Result.success(count);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "审核通过")
    public Result<Void> approveComment(@PathVariable Long id) {
        commentService.approveComment(id);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审核拒绝")
    public Result<Void> rejectComment(@PathVariable Long id) {
        commentService.rejectComment(id);
        return Result.success("已拒绝", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("删除成功", null);
    }
}
