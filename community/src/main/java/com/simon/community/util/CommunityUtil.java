package com.simon.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author zhengx
 * @version 1.0
 */
public class CommunityUtil {
    /**
     * 生成随机字符串,用于生成激活码、上传头像等
     * UUID是一种可靠的唯一标识方案，广泛用于需要跨系统、跨时间唯一标识数据的场景
     * */
    public  static String generateUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * @purpose MD5加密，加密数据
     * @param key 传入的明文
     * */
    public  static String md5(String key){
        //使用的都是common lang工具类
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return  DigestUtils.md5DigestAsHex(key.getBytes());
    }
}

