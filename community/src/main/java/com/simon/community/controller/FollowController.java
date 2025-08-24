package com.simon.community.controller;

import com.simon.community.pojo.User;
import com.simon.community.service.FollowService;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengx
 * @version 1.0
 */
@RestController
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        //关注用户
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(200,"已关注");
    }

    @RequestMapping(value = "/unfollow",method = RequestMethod.POST)
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        //关注用户
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(200,"已取消关注");
    }
}
