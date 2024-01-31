package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.constant.CommunityConstant;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private RedisTemplate redisTemplate;

    //自动触发，消费评论、点赞、关注事件，设置系统通知
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空!");
            return;
        }

        //将json格式的消息转回event类
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if(event == null){
            logger.error("消息格式错误!");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();

        //谁触发的事件
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublicMessage(ConsumerRecord record){
        logger.info("消费发帖事件");
        if(record == null || record.value() == null){
            logger.error("消息内容为空!");
            return;
        }

        //将json格式的消息转回event类
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if(event == null){
            logger.error("消息格式错误!");
            return;
        }

        //将帖子存入es服务器
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);

        //通知用户的粉丝该博主更新了
        String redisFollowerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, event.getUserId());
        Set<Integer> toAnnounceSet = redisTemplate.opsForZSet().range(redisFollowerKey, 0, -1);
        long score = System.currentTimeMillis();

        if(toAnnounceSet != null){
            for(int userId : toAnnounceSet){
                //封装通知
                Message message = new Message();
                message.setFromId(SYSTEM_USER_ID);
                Map<String, Object> content = new HashMap<>();
                content.put("userId", event.getUserId());
                content.put("postId", event.getEntityId());
                message.setConversationId(event.getTopic());
                message.setContent(JSONObject.toJSONString(content));
                message.setCreateTime(new Date());
                message.setToId(userId);
                messageService.addMessage(message);
                String redisFollowUpdatesKey = RedisKeyUtil.getFollowUpdatesKey(userId);
                //添加到收件箱
                redisTemplate.opsForZSet().add(redisFollowUpdatesKey, event.getEntityId(), score);
            }
        }
    }

    //消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDelteMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空!");
            return;
        }

        //将json格式的消息转回event类
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if(event == null){
            logger.error("消息格式错误!");
            return;
        }

        //将帖子从es服务器删除
        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }
}
