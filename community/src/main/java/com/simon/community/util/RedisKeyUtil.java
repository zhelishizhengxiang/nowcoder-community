package com.simon.community.util;

/**
 * @author zhengx
 * @version 1.0
 */
public class RedisKeyUtil {

    private static final String SPLIT=":";

    private static final String PREFIX_ENTITY_LIKE="like:entity";

    private static final String PREFIX_USER_LIKE="like:user";

    private static final String PREFIX_FOLLOWEE="followee";

    private static final String PREFIX_FOLLOWER="follower";


    /**
     * 生成某个实体的赞的key
     * eg:like:entity:entityType:entityId ->set{userId}
     * */
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    /**
     * 生成某个用户赞的总数的key
     *  like:user:userId->int
     * */
    public static String getUserLikeKey(int userId){
       return  PREFIX_USER_LIKE+SPLIT+userId;
    }

    /**
     * 生成某个用户关注的实体的key
     * followee:userId:entityType ->zset(entityId,Dote)
     * */
    public static String getFolloweeKey(int userId,int entityType){
        return  PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    /**
     * 生成某个实体拥有的粉丝的key
     * follower:entityType:entityId: zset(userId,Date)
     * */
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
}
