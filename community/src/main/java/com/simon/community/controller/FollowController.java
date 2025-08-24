package com.simon.community.controller;

import com.simon.community.pojo.Page;
import com.simon.community.pojo.User;
import com.simon.community.service.FollowService;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        //关注用户
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(200,"已关注");
    }

    @RequestMapping(value = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        //关注用户
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(200,"已取消关注");
    }

    @RequestMapping(value = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setPageSize(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followeeList = followService.findFollowees(userId, page.getOffset(), page.getPageSize());
        if(followeeList.size()>0){
            for(Map<String, Object> map:followeeList){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followeeList",followeeList);
        return "/site/followee";
    }

    /**
     查询当前用户对某一个人的关注状态
     * */
    public boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);

    }

    @RequestMapping(value = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setPageSize(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        List<Map<String, Object>> followerList = followService.findFollowers(userId, page.getOffset(), page.getPageSize());
        if(followerList.size()>0){
            for(Map<String, Object> map:followerList){
                User u=(User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followerList",followerList);
        return "/site/follower";
    }
}
