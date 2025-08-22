package com.simon.community.controller;

import com.simon.community.pojo.User;
import com.simon.community.service.LikeService;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;



    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
//    @LoginRequired
    public String like(int entityType,int entityId){
        //获取当前用户
        User user = hostHolder.getUser();
        //实现点赞
        likeService.like(user.getId(),entityType,entityId);
        //返回给页面点赞数和我是否点赞
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeStatus",status);
        map.put("likeCount",likeCount);
        return CommunityUtil.getJSONString(200,"ok",map);
    }
}
