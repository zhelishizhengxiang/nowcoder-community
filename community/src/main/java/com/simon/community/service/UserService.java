package com.simon.community.service;

import com.simon.community.dao.UserMapper;
import com.simon.community.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhengx
 * @version 1.0
 */
@Service

public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据id查询用户
     * */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
