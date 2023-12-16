package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;

import java.util.Map;

public interface UserService {
    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 用户注册
     * @param user
     * @return
     */
    Map<String, Object> register(User user);

    /**
     * 用户激活
     * @param userId
     * @param code
     * @return
     */
    int activation(int userId, String code);

    /**
     * 用户登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    Map<String, Object> login(String username, String password, int expiredSeconds);

    /**
     * 用户登出
     * @param ticket
     */
    void logout(String ticket);

    /**
     * 查询登录凭证
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 修改头像
     * @param userId
     * @param headerUrl
     * @return
     */
    int updateHeader(int userId, String headerUrl);

    /**
     * 修改密码
     * @param user
     * @param originalPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    Map<String, Object> updatePassword(User user, String originalPassword, String newPassword, String confirmPassword);

    /**
     * 通过名字找用户
     * @param name
     * @return
     */
    User findUserByName(String name);
}
