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
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

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


}
