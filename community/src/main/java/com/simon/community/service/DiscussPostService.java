package com.simon.community.service;

import com.simon.community.dao.DiscussPostMapper;
import com.simon.community.pojo.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;


    /**
     * 查询某页的数据
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    /*
    * 查询行数
    * */
    public int findDiscussPostsCount(int userId){
        return discussPostMapper.selectDiscussPostsCount(userId);
    }
}
