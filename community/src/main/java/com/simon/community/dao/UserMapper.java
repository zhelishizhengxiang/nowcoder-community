package com.simon.community.dao;

import com.simon.community.pojo.User;

public interface UserMapper {
    User selectById(Integer id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insert(User user);

    int updateStatus(int id,int status);

    int updateHeaderUrl(int id, String headerUrl);

    int updatePassword(int id,String password);



}