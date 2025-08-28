package com.simon.community.controller;

import com.simon.community.event.EventProducer;
import com.simon.community.pojo.Comment;
import com.simon.community.pojo.DiscussPost;
import com.simon.community.pojo.Event;
import com.simon.community.service.CommentService;
import com.simon.community.service.DiscussPostService;
import com.simon.community.util.CommunityConstant;
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
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加评论需要在原页面展现添加了评论的效果，展现帖子详情的controller需要携带帖子id，
     * 所以这块也需要帖子id
     */
    @RequestMapping(value = "/add/{id}",method = RequestMethod.POST )
    public String addComment(@PathVariable("id") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);

        return "redirect:/discussPost/detail/"+discussPostId;
    }
}
