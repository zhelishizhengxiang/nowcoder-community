package com.simon.community;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.simon.community.dao.mybatis")
@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init() {
        //解决netty启动冲突问题（redis和el底层都使用netty）
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
