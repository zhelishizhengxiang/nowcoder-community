package com.simon.community.util;

/**
 * @author zhengx
 * @version 1.0
 */
public interface CommunityConstant {

    /**
     * 激活状态
     * */
    int ACTIVATION_SUCCESS=0;

    /**
     * 重复激活
     * */
    int ACTIVATION_REPEAT =1;

    /**
     * 激活失败
     * */
    int ACTIVATION_FAILURE =2;

    /**
     * 默认状态的登陆凭证的有效时间,单位秒
     * */
    int DEFAULT_EXPIRED_SECONDS=3600*12;

    /**
     * 记住我状态下的登陆凭证的有效时间,单位秒
     * */
    int REMEMBER_EXPIRED_SECONDS=3600*24*30;

    /**
     * 实体类型：帖子
     * */
    int ENTITY_TYPE_POST=1;

    /**
     * 实体类型：评论
     * */
    int ENTITY_TYPE_COMMENT=2;
}
