package com.nowcoder.community.service;


import java.util.List;
import java.util.Map;

public interface FollowService {
    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    void unfollow(int userId, int entityType, int entityId);

    /**
     * 查询关注的实体数量
     * @param userId
     * @param entityType
     * @return
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 查询实体的粉丝数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findFollowerCount(int entityType, int entityId);

    /**
     * 查询当前用户是否关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean hasFollowed(int userId, int entityType, int entityId);

    /**
     * 查询某用户关注的人
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> findFollowees(int userId, int offset, int limit);

    /**
     * 查询某用户粉丝
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
