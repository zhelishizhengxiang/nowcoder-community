package com.simon.community.service;

import com.simon.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
     * @param entityUserId 写该实体的用户id
     * */
    public void like(int userId,int entityType,int entityId,int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String  userLikeKey= RedisKeyUtil.getUserLikeKey(entityUserId);
                //判断当前用户是否点过赞
                boolean isMember=operations.opsForSet().isMember(entityLikeKey,userId);
                //开启事务
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });

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

    /**
     * 查询某个用户获得点赞的数量
     * */
    public int findUserLikeCount(int userId){
        String userLikeKey= RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer) redisTemplate.opsForValue().get(userLikeKey);
        if(count==null){
            return 0;
        }else {
            return count;
        }

    }
}
