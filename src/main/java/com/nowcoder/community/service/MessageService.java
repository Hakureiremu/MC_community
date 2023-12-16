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
}
