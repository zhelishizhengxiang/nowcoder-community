package com.simon.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author zhengx
 * @version 1.0
 */
@Configuration
public class RedisConfig {


    /**
     由于默认Redis的RedisTemplate的key和value都是object（因为是泛型），所以为了方便使用我们定义自定义key为String
     * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //为创建的 RedisTemplate 设置连接工厂，使其能够通过连接工厂获取 Redis 连接
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置key序列化方式：字符串的徐磊话方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式：json
        redisTemplate.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

//        afterPropertiesSet() 方法会检查这些序列化器是否正确设置
        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }
}
