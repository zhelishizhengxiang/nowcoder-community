package com.simon.community.service;

import com.simon.community.pojo.User;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 用户关注某个实体
     * */
    public void follow(int userId,int entityType,int entityId){
        //某个实体拥有的粉丝，和某个人关注的实体都需要更新，所以需要用到事务
        redisTemplate.execute(new SessionCallback() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String  followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String  followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                //做两个key的存储操作
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                operations.exec();

                return null;
            }
        });

    }

    /**
     * 用户取消某个实体
     * */
    public void unfollow(int userId,int entityType,int entityId){
        //某个实体拥有的粉丝，和某个人关注的实体都需要更新，所以需要用到事务
        redisTemplate.execute(new SessionCallback() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String  followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String  followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                //做两个key的存储操作
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                operations.exec();

                return null;
            }
        });
    }

    /**
     * 查询某个用户关注实体的数量
     * */
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 查询实体的粉丝数量
     * */
    public long findFollowerCount(int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户对该实体的关注状态
     * */
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
        return  redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    /**
     * 查询某个用户关注的人（包括人的信息和关注的时间）
     * */
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String  followeeKey= RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        //默认按分数从小到大
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(targetIds.size()>0){
            List<Map<String,Object>> list = new ArrayList<>();
            for(Integer targetId:targetIds){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(targetId);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }
        return null;

    }


    /**
     * 查询某个用户的粉丝
     * */
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        //默认按分数从小到大
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(followerIds.size()>0){
            List<Map<String,Object>> list = new ArrayList<>();
            for(Integer followerId:followerIds){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(followerId);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followerKey, followerId);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }
        return null;

    }
}
