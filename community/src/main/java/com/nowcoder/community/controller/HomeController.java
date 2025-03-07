package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    //根据discussPostService只能查到用户id，所以需要注入下面userservie用于转换成用户名
    @Autowired
    private UserService userService;

    /**处理GET请求*/
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    //相应的是网页，所以不写@ReponseBody

    /**
     * @usage 用于获得主页显示的帖子，支持分页
     * @param model 传回来的模型
     * @param page 分页有关的信息
     * */
    public String getIndexPage(Model model, Page page) {
        // 方法调用钱,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据，
        // 返回String是视图的路径（名字），此种方式比较方便
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");//当前行路径


        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";//返回的是模板的路径
    }

}
