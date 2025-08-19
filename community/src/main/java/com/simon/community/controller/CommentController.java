package com.simon.community.controller;

import com.simon.community.pojo.Comment;
import com.simon.community.service.CommentService;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加i评论需要在原页面展现添加了评论的效果，展现帖子详情的controller需要携带帖子id，
     * 所以这块也需要帖子id
     */
    @RequestMapping(value = "/add/{id}",method = RequestMethod.POST )
    public String addComment(@PathVariable("id") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/discussPost/detail/"+discussPostId;
    }
}
