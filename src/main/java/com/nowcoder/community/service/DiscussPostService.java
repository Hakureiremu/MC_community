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
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    /**
     * 帖子数量查询
     * @param userId
     * @return
     */
    public int findRows(int userId);
}
