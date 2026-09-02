package com.vaccine.controller;

import com.vaccine.common.Result;
import com.vaccine.annotation.RequireRole;
import com.vaccine.entity.Comment;
import com.vaccine.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "发表评论")
    @PostMapping
    @RequireRole("user")
    public Result<?> addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return Result.success("评论成功");
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    @RequireRole({"user", "admin"})
    public Result<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取资讯评论列表")
    @GetMapping("/news/{newsId}")
    public Result<List<Comment>> getCommentsByNewsId(@PathVariable Long newsId) {
        return Result.success(commentService.getCommentsByNewsId(newsId));
    }

    @Operation(summary = "获取评论数量")
    @GetMapping("/news/{newsId}/count")
    public Result<Integer> getCommentCount(@PathVariable Long newsId) {
        return Result.success(commentService.getCommentCount(newsId));
    }

    @Operation(summary = "管理员获取所有评论")
    @GetMapping("/all")
    @RequireRole("admin")
    public Result<List<Comment>> getAllComments() {
        return Result.success(commentService.getAllComments());
    }

    @Operation(summary = "管理员审核评论")
    @PostMapping("/{id}/audit")
    @RequireRole("admin")
    public Result<?> auditComment(@PathVariable Long id, @RequestParam Integer status) {
        commentService.auditComment(id, status);
        return Result.success("操作成功");
    }
}
