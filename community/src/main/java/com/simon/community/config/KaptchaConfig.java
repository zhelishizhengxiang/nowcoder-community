package com.simon.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 进行kaptcha的配置
 */
@Configuration
public class KaptchaConfig {

    /**
     * 该工具类核心的接口为Producer，内含生成验证码图片的方法.实现类为DefaultKaptcha
     */
    @Bean
    public Producer producer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        //字号
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        //字体颜色RGB
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        //字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //采用的噪声类型
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        //将配置参数封装到该工具库里的Config类里
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //传入验证码配置项
        Config config=new Config(properties);
        //把参数传递给组件
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
