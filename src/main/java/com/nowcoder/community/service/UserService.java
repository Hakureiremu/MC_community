package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

public interface UserService {
    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    public User findUserById(int id);
}
