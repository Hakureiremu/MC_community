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
}
