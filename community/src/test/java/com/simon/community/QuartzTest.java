package com.simon.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest
public class QuartzTest {
    //Quartz的调度器
    @Autowired
    private Scheduler scheduler;

    //使用调度器删除定时任务
    @Test
    public void testDelete() throws SchedulerException {
        scheduler.deleteJob(new JobKey("alphaJob","alphaJobGroup"));
    }
}
