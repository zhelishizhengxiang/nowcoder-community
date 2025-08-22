package com.simon.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void TestString() {
        String key = "test:count";
        //访问string类型的数据
        redisTemplate.opsForValue().set(key,1);
        String string = redisTemplate.opsForValue().get(key).toString();
        System.out.println(string);

        System.out.println(redisTemplate.opsForValue().increment(key,10));
        System.out.println(redisTemplate.opsForValue().increment(key,10));
        System.out.println(redisTemplate.opsForValue().increment(key,7));
        System.out.println(redisTemplate.opsForValue().decrement(key,10));

    }

    @Test
    public void TestHash() {
        String key = "test:user";
        redisTemplate.opsForHash().put(key,"id",1);
        redisTemplate.opsForHash().put(key,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(key,"id"));
        System.out.println(redisTemplate.opsForHash().get(key,"username"));
    }

    @Test
    public void TestList() {
        String key = "test:ids";
        redisTemplate.opsForList().leftPush(key,101);
        redisTemplate.opsForList().leftPush(key,102);
        redisTemplate.opsForList().leftPush(key,103);
        System.out.println(redisTemplate.opsForList().range(key,0,-1));
        System.out.println(redisTemplate.opsForList().size("test:ids"));
        System.out.println(redisTemplate.opsForList().index(key,0));
        System.out.println(redisTemplate.opsForList().range(key,0,2));
        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().rightPop(key));
    }

    @Test
    public void TestSet() {
        String key = "test:teachers";
        redisTemplate.opsForSet().add(key,"刘备","张飞","刘备","关于");
        System.out.println(redisTemplate.opsForSet().size("test:teachers"));
        System.out.println(redisTemplate.opsForSet().pop("test:teachers"));
        System.out.println(redisTemplate.opsForSet().members("test:teachers"));
        System.out.println(redisTemplate.opsForSet().randomMembers("test:teachers",2));

    }

    @Test
    public void TestZSet() {
        String key = "test:students";
        redisTemplate.opsForZSet().add(key,"小明",80);
        redisTemplate.opsForZSet().add(key,"小红",60);
        redisTemplate.opsForZSet().add(key,"小花",40);
        redisTemplate.opsForZSet().add(key,"小三",20);
        System.out.println(redisTemplate.opsForZSet().zCard("test:students"));
        System.out.println(redisTemplate.opsForZSet().score("test:students","小明"));
        System.out.println(redisTemplate.opsForZSet().rank("test:students","小三"));
        System.out.println(redisTemplate.opsForZSet().range("test:students",0,-1));
        System.out.println(redisTemplate.opsForZSet().reverseRange("test:students",0,2));


    }

    @Test
    public void TestKeys() {
        System.out.println(redisTemplate.keys("*"));
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:ids",15, TimeUnit.SECONDS);
    }

    //如果多次访问同一个key，使用绑定的形式来创建redis访问对象时旧把key绑定进去
    @Test
    public void TestBoundOperations() {
        String key = "test:count";
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(key);
        //这样访问的时候就不用传该key了
        System.out.println(boundValueOperations.increment());
        System.out.println(boundValueOperations.increment());
        System.out.println(boundValueOperations.increment());
        System.out.println(boundValueOperations.increment());
        System.out.println(boundValueOperations.get());
    }

    //编程式redis事务
    @Test
    public void TestTransaction() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //SessionCallback 是 Spring Data Redis 提供的接口，
                // 用于在一个 Redis 连接中执行多个操作，适合包装事务命令

                //1.开启事务
                String key="test:tx";
                operations.multi();
                //2.添加命令到事务队列
                operations.opsForSet().add(key,"张三");
                operations.opsForSet().add(key,"李四");
                operations.opsForSet().add(key,"王五");

                //测试为执行事务前能否查到数据
                System.out.println(operations.opsForSet().members(key));
                //3.执行事务并返回
                return operations.exec();

            }
        });
        System.out.println(object);
//        String key = "test:user";
    }
}
