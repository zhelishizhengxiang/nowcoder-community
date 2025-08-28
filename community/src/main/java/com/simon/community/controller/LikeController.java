package com.simon.community.controller;

import com.simon.community.event.EventProducer;
import com.simon.community.pojo.Event;
import com.simon.community.pojo.User;
import com.simon.community.service.LikeService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author zhengx
 * @version 1.0
 */
@RestController
@Slf4j
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    /**
     * 点赞或者取消赞功能
     * @param entityType 点赞的实体对象
     * @param entityId  点赞的实体ID
     * @param entityUserId 发布该实体的用户ID
     * @param postId 点赞时所属的帖子ID
     * */
    @RequestMapping(value = "/like",method = RequestMethod.POST)
    public String like(int entityType,int entityId,int entityUserId,int postId){
        //获取当前用户
        User user = hostHolder.getUser();
        //实现点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //返回给页面点赞数和我是否点赞
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeStatus",status);
        map.put("likeCount",likeCount);

        //只在点赞的时候触发点赞事件
        if(status==1){
            Event event=new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(200,"ok",map);
    }
}
