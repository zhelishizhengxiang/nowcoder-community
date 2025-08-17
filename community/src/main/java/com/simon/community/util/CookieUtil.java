package com.simon.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zhengx
 * @version 1.0
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name) {
        if(request ==null || name==null){
            throw new IllegalArgumentException("参数未空");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
