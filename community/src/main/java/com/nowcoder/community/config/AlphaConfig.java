package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author zhengx
 * @version 1.0
 */
/*JAR（java achieve） 文件可以将一个完整的 Java 应用程序或库的所有相关文件打包成一个文件，
方便在不同的环境中进行分发和部署。*/
/**
 * 背景：有时候希望Spring容器管理的不是自己创建的类，
 * 而是在Spring容器中装配的是第三方的Bean，或者是是在jar包里是别人写的,
 * 此时就没办法对该Bean进行加注解来标记为Bean了*/

/**那么此时的做法就为：写一个配置类，在配置类中通过@Bean注解来解决该问题
 * @Bean 注解通常用于在配置类中定义 Bean，*/
//@Configuration标记该类为 配置类，允许通过 @Bean 注解显式定义 Bean。
@Configuration
public class AlphaConfig {
    //假设把Java自带的SimpleDataFormat类装配到容器当中
    @Bean//方法名就是该Bean的名字
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
