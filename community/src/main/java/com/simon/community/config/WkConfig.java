package com.simon.community.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author zhengx
 * @version 1.0
 */
@Slf4j
@Configuration
public class WkConfig {

    @Value("${wk.image.command}")
    private String command;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    public void init(){
        //创建wk图片目录
        File file = new File(wkImageStorage);
        if(!file.exists()){
            file.mkdir();
            log.info("创建wk图片目录"+wkImageStorage);
        }

    }
}
