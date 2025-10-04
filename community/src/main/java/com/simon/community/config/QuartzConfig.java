package com.simon.community.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author zhengx
 * @version 1.0
 *
 */
//配置->数据库->第二次回去数据库进行调用
@Configuration
public class QuartzConfig {

    //配置Job（任务）JobDetail对象
//    @Bean
//    public JobDetail jobDetail() {
//        //newJob方法就是在绑定要运行的Job接口实现类,需要实现类的反射做参数
//        return JobBuilder.newJob(QuartzAddStock.class)
//                // 给当前 JobDetail对象在调度环境中起名.
//                // 并且还可以设置组名。多个任务可以同属于同一个组，可以设置组名
//                .withIdentity("addStock","alphaJobGroup")
//                // 即使没有触发器绑定当前JobDetail对象,也不会被删除
//                .storeDurably()
//                //当 Quartz 调度器意外停止（如崩溃、重启）后，是否自动恢复并重新执行那些在停止时正在运行的 Job 实例
//                .requestRecovery(true)
//                .build();
//    }

    //配置触发器：能够描述触发指定job的规则,SimpleTrigger是简单规则触发器。CronTrigger复杂规则触发器
//    @Bean
//    CronTrigger cronTrigger(JobDetail jobDetail) {
//        // 定义Cron表达式
//        CronScheduleBuilder cron=
//                CronScheduleBuilder.cronSchedule("0 0/2 * * * ?");
//        return TriggerBuilder.newTrigger()
//                // 绑定要运行的JobDetail对象
//                .forJob(jobDetail)
//                // 为触发器起名
//                .withIdentity("addStockTrigger","alphaJobGroup")
//                // 绑定cron表达式
//                .withSchedule(cron)
//                .build();
//    }

}
