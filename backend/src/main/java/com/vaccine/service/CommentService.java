package com.vaccine.service;

import com.vaccine.common.UserContext;
import com.vaccine.entity.Comment;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 发表评论
     */
    public void addComment(Comment comment) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw BusinessException.of(401, "请先登录");
        }
        comment.setUserId(userId);
        comment.setStatus(1); // 直接发布，如需审核改为0
        commentMapper.insert(comment);
    }

    /**
     * 删除评论（只能删除自己的）
     */
    public void deleteComment(Long id) {
        Long userId = UserContext.getUserId();
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw BusinessException.of("评论不存在");
        }
        // 只有评论作者或管理员可以删除
        if (!comment.getUserId().equals(userId) && !UserContext.isAdmin()) {
            throw BusinessException.of(403, "无权删除此评论");
        }
        commentMapper.updateStatus(id, 2); // 标记为已删除
    }

    /**
     * 获取资讯的评论列表
     */
    public List<Comment> getCommentsByNewsId(Long newsId) {
        return commentMapper.findByNewsId(newsId);
    }

    /**
     * 获取评论数量
     */
    public int getCommentCount(Long newsId) {
        return commentMapper.countByNewsId(newsId);
    }

    /**
     * 管理员获取所有评论
     */
    public List<Comment> getAllComments() {
        return commentMapper.findAll();
    }

    /**
     * 管理员审核评论
     */
    public void auditComment(Long id, Integer status) {
        commentMapper.updateStatus(id, status);
    }
}
