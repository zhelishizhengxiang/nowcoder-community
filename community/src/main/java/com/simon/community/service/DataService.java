package com.simon.community.service;

import com.simon.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    /**
     *  将指定IP计入UV
     * */
    public void recordUv(String ip){
        String key= RedisKeyUtil.getUvKey(sdf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key,ip);
    }

    /**
     * 统计指定日期范围内的UV
     * */
    public long calculateUv(Date start,Date end){
        if(start==null||end==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //整理日期范围内的key
        List<String> keyList=new ArrayList<>();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key=RedisKeyUtil.getUvKey(sdf.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }

        //合并数据
        String redisKey=RedisKeyUtil.getUvKey(sdf.format(start),sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray(new String[keyList.size()]));

        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }


    /**
     * 将指定用户计入DAU
     * */
    public void recordDau(int userId){
        String key=RedisKeyUtil.getDauKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(key,userId,true);
    }

    /**
     * 统计指定日期内的DAU
     * */
    public long  calculateDau(Date start,Date end){
        if(start==null||end==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //整理日期范围内的key
        List<byte[]> keyList=new ArrayList<>();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key=RedisKeyUtil.getDauKey(sdf.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        //合并数据，进行或运算
        String redisKey=RedisKeyUtil.getDauKey(sdf.format(start),sdf.format(end));
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });

    }

}
