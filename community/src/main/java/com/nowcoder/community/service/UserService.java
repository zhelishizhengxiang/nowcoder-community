package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /*根据用户id查询用户名，因为页面上肯定不会显示user-id，而是显示用户名*/
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

}
