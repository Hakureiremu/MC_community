package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.apache.juli.logging.Log;

@Mapper
public interface LoginTicketMapper {
    /**
     * 插入登录凭证
     * @return
     */
    @Insert("insert into login_ticket(user_id, ticket, status, expired) " +
            "values (#{userId}, #{ticket}, #{status}, #{expired})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 查询登录凭证
     * @param ticket
     * @return
     */
    @Select("select id, user_id, ticket, status, expired from login_ticket " +
            "where ticket = #{ticket}")
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改登录状态
     * @param ticket
     * @param status
     * @return
     */
    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    int updateStatus(String ticket, int status);

}
