package com.simon.community.controller;

import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.Page;
import com.simon.community.pojo.User;
import com.simon.community.service.DiscussPostService;
import com.simon.community.service.LikeService;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
public class IndexController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 获取主页数据,通过page来封装请求数据
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String getIndexpage(Model model, Page page) {
        //获取数据总数和路径
        page.setRows(discussPostService.findDiscussPostsCount(0));
        page.setPath("index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getPageSize());
        //装入帖子和发帖子的人的数据
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (discussPosts != null && discussPosts.size() > 0) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                mapList.add(map);
            }
        }
        model.addAttribute("discussPosts", mapList);
        return "index";
    }

    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String getErrorpage() {
        return "/error/500";
    }
}
