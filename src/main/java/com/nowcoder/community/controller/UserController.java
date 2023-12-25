package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.constant.CommunityConstant;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.AliOssUtil;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AliOssUtil aliOssUtil;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    //账号设置
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //头像设置
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        try {
            //重命名传入图片，截取后缀
            String filename = headerImage.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf("."));
            if(StringUtils.isBlank(suffix)){
                model.addAttribute("error", "文件格式不正确！");
                return "/site/setting";
            }

            //生成随机文件名
            filename = CommunityUtil.generateUUID() + suffix;
            //上传阿里云，返回文件请求路径
            String filePath = aliOssUtil.upload(headerImage.getBytes(), filename);

            //更新当前用户的头像路径（请求路径）
            //http://localhost:8080/community/user/header/xxx.png
            User user = hostHolder.getUser();
//        String headerUrl = domain + contextPath + "/user/header/" + filename;
            userService.updateHeader(user.getId(), filePath);

        } catch (IOException e) {
            logger.error("上传文件失败：{}", e);
        }
//        //确定文件存放路径
//        File dest = new File(uploadPath + "/" + filename);
//        try {
//            //存储文件
//            headerImage.transferTo(dest);
//        } catch (IOException e) {
//            logger.error("上传文件失败:" + e.getMessage());
//            throw new RuntimeException("上传文件失败，服务器异常！", e);
//        }
        return "redirect:/index";
    }

//    //使用流向浏览器输出二进制文件
//    //显示头像
//    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
//    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
//        //服务器存放路径
//        fileName = uploadPath + "/" + fileName;
//        //获取后缀
//        String suffix = fileName.substring(fileName.lastIndexOf("."));
////        System.out.println(suffix);
//        //响应图片
//        response.setContentType("image/" + suffix);
//        try (
//            ServletOutputStream os = response.getOutputStream();
//            FileInputStream fis= new FileInputStream(fileName);
//        ) {
//            byte[] buffer = new byte[1024];
//            int b = 0;
//            while ((b = fis.read(buffer)) != -1) {
//                os.write(buffer, 0, b);
//            }
//        } catch (IOException e) {
//            logger.error("读取头像失败："+e.getMessage());
//        }
//    }

    //修改密码
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String originalPassword, String newPassword, String confirmPassword, Model model){
        Map<String, Object> map = userService.updatePassword(hostHolder.getUser(), originalPassword, newPassword, confirmPassword);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "修改密码成功！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("originalPasswordMsg", map.get("originalPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
    }

    //个人主页(任意人)
    @RequestMapping(path = "/profile/{userId}" , method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user", user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //当前登录用户是否关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
