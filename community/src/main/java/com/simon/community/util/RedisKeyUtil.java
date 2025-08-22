package com.simon.community.util;

/**
 * @author zhengx
 * @version 1.0
 */
public class RedisKeyUtil {

    private static final String SPLIT=":";

    private static final String PREFIX_ENTITY_LIKE="like:entity";

    private static final String PREFIX_USER_LIKE="like:user";

    /**
     * 生成某个实体的赞的key
     * */
//    eg:like:entity:entityType:entityId ->set{userId}
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    /**
     * 生成某个用户赞的总数的key
     * */
    //like:user:userId->int
    public static String getUserLikeKey(int userId){
       return  PREFIX_USER_LIKE+SPLIT+userId;
    }
}
