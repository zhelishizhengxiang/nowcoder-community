package com.simon.community.controller;

import com.simon.community.event.EventProducer;
import com.simon.community.pojo.*;
import com.simon.community.service.CommentService;
import com.simon.community.service.DiscussPostService;
import com.simon.community.service.LikeService;
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

import java.util.*;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    //获取当前用户
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer  eventProducer;

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

        //触发发帖事件，将帖子存入es
        Event event=new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(200,"发布成功");
    }

    /**
     * 查看帖子详情和对应评论数据
     * */
    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //查询user，得到用户名
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //帖子的点赞数量和状态
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        //是否登录判断显示内容
        int likeStatus=-1;
        if(hostHolder.getUser()==null)
            likeStatus=0;
        else
            likeStatus= likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);

        //查评论的分页信息
        page.setPageSize(5);
        page.setPath("/discussPost/detail/"+post.getId());
        //直接从帖子里取评论总数
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getPageSize());
        //评论VO列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for(Comment comment : commentList){
                //评论Vo
                Map<String,Object> commentVoMap = new HashMap<>();
                //评论
                commentVoMap.put("comment",comment);
                //评论作者
                commentVoMap.put("user",userService.findUserById(comment.getUserId()));
                //评论的点赞数量和状态
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                //是否登录判断显示内容
                likeStatus=-1;
                if(hostHolder.getUser()==null)
                    likeStatus=0;
                else
                    likeStatus= likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVoMap.put("likeCount",likeCount);
                commentVoMap.put("likeStatus",likeStatus);
                //回复列表
                List<Comment> ReplyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(ReplyList!=null){
                    for(Comment reply : ReplyList){
                        Map<String,Object> replyVoMap = new HashMap<>();
                        //回复
                        replyVoMap.put("reply",reply);
                        //作者
                        replyVoMap.put("user",userService.findUserById(reply.getUserId()));
                        //帖子的评论无指向性，只有回复有指向性
                        //回复的目标用户
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVoMap.put("target",target);
                        //回复的点赞数量和状态
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        //是否登录判断显示内容
                        likeStatus=-1;
                        if(hostHolder.getUser()==null)
                            likeStatus=0;
                        else
                            likeStatus= likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVoMap.put("likeCount",likeCount);
                        replyVoMap.put("likeStatus",likeStatus);
                        replyVoList.add(replyVoMap);
                    }
                }
                commentVoMap.put("replies",replyVoList);
                //这条评论的回复数量
                int replyCount = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVoMap.put("replyCount",replyCount);
                commentVoList.add(commentVoMap);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
