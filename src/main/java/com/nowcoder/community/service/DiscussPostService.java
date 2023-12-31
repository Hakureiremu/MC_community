package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    /**
     * 帖子分页查询
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 帖子数量查询
     * @param userId
     * @return
     */
    int findRows(int userId);

    /**
     * 发布帖子
     * @param discussPost
     * @return
     */
    int addDiscussPost(DiscussPost discussPost);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    DiscussPost findDiscussPostById(int id);

    /**
     * 更新帖子的评论数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);

    /**
     * 设置帖子是否置顶
     * @param id
     * @param type
     * @return
     */
    int updateType(int id, int type);

    /**
     * 设置帖子是否加精
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
