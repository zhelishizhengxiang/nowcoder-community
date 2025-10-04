package com.simon.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhengx
 * @version 1.0
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

}
