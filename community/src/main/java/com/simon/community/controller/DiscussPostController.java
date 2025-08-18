package com.simon.community.controller;

import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.User;
import com.simon.community.service.DiscussPostService;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    //获取当前用户
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add" ,method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        //查看是否登录
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        post.setScore(0.0);
        post.setType(0);
        post.setStatus(0);
        post.setCommentCount(0);
        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJSONString(200,"发布成功");
    }
}
