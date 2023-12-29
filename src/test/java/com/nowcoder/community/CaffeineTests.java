//package com.nowcoder.community;
//
//import com.nowcoder.community.entity.DiscussPost;
//import com.nowcoder.community.service.DiscussPostService;
//import org.checkerframework.checker.units.qual.A;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Date;
//
////@SpringBootTest
//public class CaffeineTests {
//    @Autowired
//    private DiscussPostService discussPostService;
//
//    @Test
//    public void initDataForTest(){
//        for (int i = 0; i < 300000; i++) {
//            DiscussPost post = new DiscussPost();
//            post.setUserId(111);
//            post.setTitle("25届该何去何从");
//            post.setContent("找实习要求实习经历，这不就成了死胡同了么？举报了哥，你真的让我破防了。");
//            post.setCreateTime(new Date());
//            post.setScore(Math.random() * 2000);
//            discussPostService.addDiscussPost(post);
//        }
//    }
//
//
//    @Test
//    public void testCache(){
//
//        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
//        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
//        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
//        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 0));
//
//    }
//}
