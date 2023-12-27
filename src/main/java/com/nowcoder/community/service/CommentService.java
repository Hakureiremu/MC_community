package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;

import java.util.List;

public interface CommentService {
    /**
     * 通过实体id查找对应评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 获取评论数量
     * @param entityType
     * @param entityId
     * @return
     */
    int findCountByEntity(int entityType, int entityId);

    /**
     * 添加评论
     * @param comment
     * @return
     */
    int addComment(Comment comment);

    /**
     * 根据id查找评论
     * @param commentId
     * @return
     */
    Comment findCommentById(int commentId);

    /**
     * 根据用户id查找评论
     * @param userId
     * @return
     */
    List<Comment> findCommentByUserId(int userId, int offset, int limit);
}
