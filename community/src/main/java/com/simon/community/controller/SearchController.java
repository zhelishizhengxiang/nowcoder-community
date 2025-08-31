package com.simon.community.controller;

import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.Page;
import com.simon.community.service.ElasticsearchService;
import com.simon.community.service.LikeService;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
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
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //search?keyword=xxx
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        SearchHits<DiscussPost> searchHits = elasticsearchService.searchDiscussPost(keyword, page.getCurrentPage() - 1, page.getPageSize());

        //聚合数据
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(searchHits!=null){
            for (SearchHit<DiscussPost> searchHit : searchHits.getSearchHits()) {
                Map<String,Object> map=new HashMap<>();
                map.put("post",searchHit.getContent());
                //存入帖子作者
                map.put("user",userService.findUserById(searchHit.getContent().getUserId()));
                //存入提欸子点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,searchHit.getContent().getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        //搜索词回显
        model.addAttribute("keyword",keyword);
        //设置分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchHits==null?0: (int) searchHits.getTotalHits());
        return "/site/search";
    }
}
