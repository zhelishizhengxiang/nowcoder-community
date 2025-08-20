package com.simon.community.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author zhengx
 * @version 1.0
 */
public class CommunityUtil {
    /**
     *  生成随机字符串
     * */
    public  static String generateUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     *  MD5加密，加密数据
     * @param key 传入的明文
     * */
    public  static String md5(String key){
        //使用的都是common lang工具类
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return  DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     *  封装前后端交互的数据格式
     * @param code 状态编码
     * @param msg 提示信息
     * @param map  业务数据
     * */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        //先封装成JSON对象，之后封装成JSON串
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        //map中的内容以键值对的形式放入json
        if(map != null) {
            for(String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        //先封装成JSON对象，之后封装成JSON串
        return  getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return  getJSONString(code,null,null);
    }


}

