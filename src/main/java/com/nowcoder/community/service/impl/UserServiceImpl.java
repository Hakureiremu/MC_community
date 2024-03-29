package com.nowcoder.community.service.impl;

import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.nowcoder.community.constant.CommunityConstant;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService, CommunityConstant{
    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //持有用户信息
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    @Override
    public User findUserById(int id) {
        //return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        if(user.getPassword().length() < 8){
            map.put("passwordMsg", "密码不能小于八位！");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg", "账号已存在！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg", "邮箱已被注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nocoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //邮箱激活
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    @Transactional
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg", "账号不存在！");
            return map;
        }

        if(user.getStatus()==0){
            map.put("usernameMsg", "账号未激活！");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket, expiredSeconds, TimeUnit.SECONDS);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl){
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    @Transactional
    public Map<String, Object> updatePassword(User user, String originalPassword, String newPassword, String confirmPassword){
        Map<String, Object> map = new HashMap<>();

        //先做密码合法性判断
        if(originalPassword.length()<8){
            map.put("originalPasswordMsg", "密码不能小于八位！");
            return map;
        }

//        if(user.getPassword() != originalPassword){
//            map.put("originalPasswordMsg", "原密码错误！");
//            return map;
//        }
//
//        if (user.getPassword() == newPassword){
//            map.put("newPasswordMsg", "新密码与原密码相同！");
//            return map;
//        }

        if(!newPassword.equals(confirmPassword)) {
            map.put("confirmPasswordMsg", "两次输入的密码不一致！");
            return map;
        }

        if(!user.getPassword().equals(CommunityUtil.md5(originalPassword+user.getSalt()))){
            map.put("originalPasswordMsg", "原始密码错误!");
            return map;
        }

        if(user.getPassword().equals(CommunityUtil.md5(newPassword+user.getSalt()))){
            map.put("newPasswordMsg", "新密码和原始密码一致，请重新输入！");
            return map;
        }

        //修改密码
        userMapper.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        clearCache(user.getId());
        return map;
    }

    @Override
    public User findUserByName(String name) {
        return userMapper.selectByName(name);
    }

    //优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }

    //取不到则初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

//    //查询用户权限
//    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
//        User user = this.findUserById(userId);
//
//        List<GrantedAuthority> list = new ArrayList<>();
//        //获取用户权限
//        list.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                switch (user.getType()){
//                    case 1:
//                        return AUTHORITY_ADMIN;
//                    case 2:
//                        return AUTHORITY_MODERATOR;
//                    default:
//                        return AUTHORITY_USER;
//                }
//            }
//        });
//        return list;
//    }
}
