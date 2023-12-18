package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface MessageService {

    /**
     * 对话列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 查找对话数量
     * @param userId
     * @return
     */
    int findConversationCount(int userId);

    /**
     * 私信列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 总私信数量
     * @param conversationId
     * @return
     */
    int findLetterCount(String conversationId);

    /**
     * 查找未读私信数量
     * @param userId
     * @param conversationId
     * @return
     */
    int findLetterUnreadCount(int userId, String conversationId);

    /**
     * 添加私信
     * @param message
     * @return
     */
    int addMessage(Message message);

    /**
     * 读取消息
     * @param ids
     * @return
     */
    int readMessage(List<Integer> ids);

    /**
     * 查询最新通知
     * @param userId
     * @param topic
     * @return
     */
    Message findLatestNotice(int userId, String topic);

    /**
     * 查询通知数量
     * @param userId
     * @param topic
     * @return
     */
    int findNoticeCount(int userId, String topic);

    /**
     * 查询未读通知数量
     * @param userId
     * @param topic
     * @return
     */
    int findUnreadNoticeCount(int userId, String topic);

    /**
     * 查询通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findNotices(int userId, String topic, int offset, int limit);
}
