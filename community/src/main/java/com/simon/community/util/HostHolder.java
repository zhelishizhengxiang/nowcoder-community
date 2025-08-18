package com.simon.community.util;

import com.simon.community.pojo.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 * @author zhengx
 * @version 1.0
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<User>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    /*
    * 线程结束清理
    */
    public void clear(){
        users.remove();
    }
}
