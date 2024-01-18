package com.nowcoder.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.RedisKeyUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

//    //缓存数据的数量
//    @Value("${caffeine.posts.max-size}")
//    private int maxSize;
//
    //缓存过期时间
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds1;

    //二级缓存过期时间
    @Value("${spring.redis.cache.expire-seconds}")
    private int expireSeconds2;

    //Caffeine核心接口： Cache, LoadingCache, AsyncLoadingCache

//    //帖子列表的缓存
//    private LoadingCache<String, List<DiscussPost>> postListCache;
//
//    //帖子总数的缓存
//    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //预热缓存
    }

//    @PostConstruct
//    public void init(){
//        //初始化帖子列表缓存
//        postListCache = Caffeine.newBuilder()
//                .maximumSize(maxSize)
//                .expireAfterWrite(expireSeconds1, TimeUnit.SECONDS)
//                .build(new CacheLoader<String, List<DiscussPost>>() {
//                    @Override
//                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
//                        if (key == null || key.length() == 0){
//                            throw new IllegalArgumentException("参数错误!");
//                        }
//
//                        String[] params = key.split(":");
//                        if(params == null || params.length != 2){
//                            throw new IllegalArgumentException("参数错误！");
//                        }
//
//                        int offset = Integer.valueOf(params[0]);
//                        int limit = Integer.valueOf(params[1]);
//
//                        String redisKey = RedisKeyUtil.getHomeKey(offset, limit);
//
//                        //二级缓存
//                        List<DiscussPost> cachedData = (List<DiscussPost>) redisTemplate.opsForValue().get(redisKey);
//                        if(cachedData!=null){
//                            logger.debug("load post list from Redis");
//                            return cachedData;
//                        }
//                        logger.debug("load post list from DB.");
//                        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
//                        redisTemplate.opsForValue().set(redisKey, list, expireSeconds2, TimeUnit.SECONDS);
//                        return list;
//                    }
//                });
//
//        //初始化帖子总数缓存
//        postRowsCache = Caffeine.newBuilder()
//                .maximumSize(maxSize)
//                .expireAfterWrite(expireSeconds1, TimeUnit.SECONDS)
//                .build(new CacheLoader<Integer, Integer>() {
//                    @Override
//                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
//                        String redisKey = RedisKeyUtil.getHomeNum();
//                        Integer cachedRows = (Integer) redisTemplate.opsForValue().get(RedisKeyUtil.getHomeNum());
//                        if(cachedRows != null){
//                            logger.debug("load post num from Redis");
//                            return cachedRows;
//                        }
//
//                        logger.debug("load post list from DB.");
//                        Integer num = discussPostMapper.selectDiscussPostRows(key);
//                        redisTemplate.opsForValue().set(redisKey, num, expireSeconds2, TimeUnit.SECONDS);
//                        return num;
//                    }
//                });
//    }

    //访问首页/用户查看自己帖子
    @Override
    @Cacheable(cacheNames = "postListCache", cacheManager = "caffeineCacheManager", condition = "#userId == 0 && #orderMode == 1")
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
//        if(userId == 0 && orderMode == 1){
//            return postListCache.get(offset + ":" + limit);
//        }
        //查询redis
        String redisKey = RedisKeyUtil.getHomeKey(offset, limit);
        List<DiscussPost> posts = new ArrayList<>();

        if(userId == 0 && orderMode == 1){
            Object postList = redisTemplate.opsForValue().get(redisKey);
            if(Objects.nonNull(postList)){
                logger.info("load post list from redis");
                return (List<DiscussPost>) postList;
            }
            logger.debug("load post list from DB");
            posts = discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
            redisTemplate.opsForValue().set(redisKey,posts);
        }else{
            logger.debug("load post list from DB");
            posts = discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
        }

        return posts;
    }

    @Override
    @Cacheable(cacheNames = "postListNumCache", condition = "#userId == 0")
    public int findRows(int userId) {
//        if(userId == 0){
//            return postRowsCache.get(userId);
//        }
        //查询redis
        String redisKey = RedisKeyUtil.getHomeNum();
        int num=0;
        if(userId == 0){
            Object postNum = redisTemplate.opsForValue().get(redisKey);
            if(Objects.nonNull(postNum)){
                logger.info("load post list from redis");
                return (int) postNum;
            }
            logger.debug("load post row from DB");
            num = discussPostMapper.selectDiscussPostRows(userId);
            redisTemplate.opsForValue().set(redisKey, num);
        }else{
            logger.debug("load post list from DB");
            num = discussPostMapper.selectDiscussPostRows(userId);
        }

        return num;
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        //转义html标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

}
