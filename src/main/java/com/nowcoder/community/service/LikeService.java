package com.nowcoder.community.service;

public interface LikeService {
    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    void like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某实体点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某人对某实体点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * 查询某人获赞数
     * @param userId
     * @return
     */
    int findUserLikeCount(int userId);


}
