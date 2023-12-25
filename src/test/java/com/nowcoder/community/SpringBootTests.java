package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import java.util.Date;

//单元测试
//整体执行，所有方法都会跑一遍
@SpringBootTest(classes = KriyesApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringBootTests {
    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    //类初始化前
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }

    //类初始化后
    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    //调用方法前
    //初始化数据
    @Before
    public void before(){
        System.out.println("before");
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    @Test
    public void testFindById(){
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());

        //使用断言判断查询是否符合初始化的数据，否则抛异常
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
    }

    @Test
    public void testUpdateScore(){
        int rows = discussPostService.updateScore(data.getId(), 2000.00);

        Assert.assertEquals(1, rows);

        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2);
    }

    //调用方法后
    //删除数据
    @After
    public void after(){
        System.out.println("after");
        discussPostService.updateStatus(data.getId(), 2);
    }

//    @Test
//    public void test1(){
//        System.out.println("test1");
//    }
//
//    @Test
//    public void test2(){
//        System.out.println("test2");
//    }


}
