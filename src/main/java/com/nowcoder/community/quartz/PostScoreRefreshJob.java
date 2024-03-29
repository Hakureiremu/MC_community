package com.nowcoder.community.quartz;

import com.nowcoder.community.constant.CommunityConstant;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化失败", e);
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            logger.info("没有需要刷新的帖子!");
            return;
        }

        logger.info("任务开始 刷新帖子分数：" + operations.size());
        while (operations.size()>0){
            this.refresh((Integer) operations.pop());
        }
        logger.info("任务结束");
    }

    private void refresh(int postId){
        //得到状态改变过的帖子
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if(post == null){
            logger.error("该贴已不存在: id" + postId);
            return;
        }

        //是否精华
        boolean wonderful = post.getStatus() == 1;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(1, w)) +
                (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        //更新帖子分数
        discussPostService.updateScore(postId, score);

        //更新缓存
        logger.info("更新缓存");
        List<DiscussPost> hotPosts1 = discussPostMapper.selectDiscussPosts(0, 0, 10, 1);
        List<DiscussPost> hotPosts2 = discussPostMapper.selectDiscussPosts(0, 10, 10, 1);
        redisTemplate.opsForValue().set(RedisKeyUtil.getHomeKey(0,10), hotPosts1);
        redisTemplate.opsForValue().set(RedisKeyUtil.getHomeKey(10,10), hotPosts2);

        //同步搜索数据
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);
    }
}
