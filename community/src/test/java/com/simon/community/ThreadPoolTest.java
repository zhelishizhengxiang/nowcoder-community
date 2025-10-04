package com.simon.community;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengx
 * @version 1.0
 */
@Slf4j
@SpringBootTest(classes = CommunityApplication.class)
public class ThreadPoolTest {

    //演示JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //注入spring自带的线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    private  void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
    }

    //1.演示JDK普通线程池的使用
    @Test
    public void testExecutorService() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }
        sleep(2000);
    }

    //2.演示JDK定时任务线程池
    @Test
    public void testScheduledExecutorService() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(runnable,1,1, TimeUnit.SECONDS);
        sleep(20000);
    }

    //3.演示Spring自带线程池
    @Test
    public void testThreadPoolTaskExecutor() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("hello ThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(runnable);

        }
        sleep(2000);
    }

    //4.演示spring定时任务线程池
    @Test
    public void testScheduledThreadPoolTaskExecutor() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("hello ScheduledThreadPoolTaskExecutor");
            }
        };

        //初始化开始的时间1
        Date startTime = new Date(System.currentTimeMillis()+3000);
        threadPoolTaskScheduler.scheduleAtFixedRate(runnable,startTime,1000);
        sleep(10000);
    }

    //Spring普通线程池的简便用法：在某个方法上加@Async注解（前提是在配置类配置了@EnableAsync），
    // 当调用该方法时，该注解可以让被标记的方法在独立的线程中执行（使用spring中的线程池中的线程执行），避免阻塞当前主线程，从而提高程序的并发效率

    //Spring定时线程池的渐变用法：在方法上加@Scheduled注解（前提是在配置类配置了@EnableScheduling），
    // 可以通过里面的initialDelay参数来指定初始的延时时间，fixRate属性来指定执行的间隔时间
    //在这样的情况下，只要程序跑起来，该方法就会定期执行

}
