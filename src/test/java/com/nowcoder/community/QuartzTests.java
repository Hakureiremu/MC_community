package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuartzTests {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJob(){
        try {
            boolean re = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
            System.out.println(re);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
