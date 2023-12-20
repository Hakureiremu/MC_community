package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticSearchService {
    /**
     * 将贴子保存到ES服务器
     * @param post
     */
    void saveDiscussPost(DiscussPost post);

    /**
     * 从ES服务器删除帖子
     * @param id
     */
    void deleteDiscussPost(int id);

    /**
     * 从ES服务器查询帖子
     * @param keyword
     * @param current
     * @param limit
     * @return
     */
    Page<DiscussPost> searchDisucssPost(String keyword, int current, int limit);
}
