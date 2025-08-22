package com.simon.community.service;

import com.simon.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 实现点赞的业务方法
     * @param userId 点赞的用户
     * @param entityType 点赞的实体类型
     * @param entityId 点赞的实体id
     * */
    public void like(int userId,int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        //set{userId}
        //判断当前用户是否点过赞
        if(redisTemplate.opsForSet().isMember(entityLikeKey,userId)){
            //取消点赞
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else{
            //添加点赞
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    /**
     * 查询某实体点赞的数量
     * */
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 判断某个是否对某个实体的点赞状态
     * @return 1 已经点赞
     * @return 0 未点赞
     * */
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
