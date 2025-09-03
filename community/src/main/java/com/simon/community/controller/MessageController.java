package com.simon.community.controller;

import com.alibaba.fastjson2.JSONObject;
import com.simon.community.pojo.Message;
import com.simon.community.pojo.Page;
import com.simon.community.pojo.User;
import com.simon.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 私信列表
     * */
    @RequestMapping(value ="/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        page.setPageSize(5);
        page.setPath("/letter/list");
        User user = hostHolder.getUser();
        page.setRows(messageService.findConversationsCount(user.getId()));
        //会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getPageSize());
        //总的未读的私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        List<Map<String,Object>> conversations=new ArrayList<>();
        if(conversationsList!=null&&conversationsList.size()>0){
            for(Message message:conversationsList){
                Map<String,Object> map=new HashMap<>();
                //会话
                map.put("conversation",message);
                //未读消息数量
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //会话消息数量
                map.put("letterCount",messageService.findLettersCount(message.getConversationId()));
                //私聊用户
                int targetId=(user.getId()==message.getFromId()?message.getToId():message.getFromId());
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);

            }
        }
        model.addAttribute("conversations",conversations);

        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }

    /**
     * 私信内容
     * */
    @RequestMapping(value = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(Model model, Page page, @PathVariable("conversationId") String conversationId){
        page.setPageSize(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLettersCount(conversationId));
        User user = hostHolder.getUser();

        //私信列表
        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getPageSize());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(lettersList!=null&&lettersList.size()>0){
            for(Message letter:lettersList){
                Map<String,Object> map=new HashMap<>();
                //私信内容
                map.put("letter",letter);
                //发信息的用户信息
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //对话用户的信息
        model.addAttribute("targetUser",userService.findUserById(getLetterTargetUser(conversationId)));

        //对该对话中的所有消息进行已读
        List<Integer> ids = getLetterUnreadIds(lettersList);
        if(ids!=null&&ids.size()>0){
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";

    }

    /**
     * 获取对方发送给本人得所有消息id
     * */
    private List<Integer> getLetterUnreadIds(List<Message> lettersList){
        List<Integer> ids=new ArrayList<>();
        if(lettersList!=null&&lettersList.size()>0){
            for(Message letter:lettersList){
                if(letter.getStatus()==0 && letter.getToId().equals(hostHolder.getUser().getId())){
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 从conversation_id中获取对话用户id
     * */
    private int getLetterTargetUser(String conversationId){
        String[] s = conversationId.split("_");
        int id0=Integer.parseInt(s[0]);
        int id1=Integer.parseInt(s[1]);
        return id0==hostHolder.getUser().getId()?id1:id0;
    }

    /**
     * 发送私信
     * */
    @RequestMapping(value = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        User toUser = userService.findUserByName(toName);
        if(toUser==null){
            return CommunityUtil.getJSONString(403,"目标用户不存在！");
        }
        message.setToId(toUser.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(200);
    }

    /**
     * 显示通知列表
     * */
    @RequestMapping(value = "/notice/list",method = RequestMethod.GET)
    public String noticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论的通知信息
        Message latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String,Object> commentMap=new HashMap<>();
        if(latestNotice!=null){
            commentMap.put("latestNotice",latestNotice);
            //将内容反序列化
            String content= HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            commentMap.put("user",userService.findUserById((Integer) data.get("userId")));
            commentMap.put("entityType",data.get("entityType"));
            commentMap.put("entityId",data.get("entityId"));
            commentMap.put("postId",data.get("postId"));
            //评论的总通知书数和未读数存入map
            int count=messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            commentMap.put("count",count);
            int unreadCount=messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            commentMap.put("unreadCount",unreadCount);
        }
        model.addAttribute("commentMap",commentMap);

        //查询点赞的通知信息
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        Map<String,Object> likeMap=new HashMap<>();
        if(latestNotice!=null){
            likeMap.put("latestNotice",latestNotice);
            //将内容反序列化
            String content= HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            likeMap.put("user",userService.findUserById((Integer) data.get("userId")));
            likeMap.put("entityType",data.get("entityType"));
            likeMap.put("entityId",data.get("entityId"));
            likeMap.put("postId",data.get("postId"));
            //评论的总通知书数和未读数存入map
            int count=messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            likeMap.put("count",count);
            int unreadCount=messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            likeMap.put("unreadCount",unreadCount);
        }
        model.addAttribute("likeMap",likeMap);
        //查询关注的通知信息
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        Map<String,Object> followMap=new HashMap<>();
        if(latestNotice!=null){
            followMap.put("latestNotice",latestNotice);
            //将内容反序列化
            String content= HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            followMap.put("user",userService.findUserById((Integer) data.get("userId")));
            followMap.put("entityType",data.get("entityType"));
            followMap.put("entityId",data.get("entityId"));
            //评论的总通知书数和未读数存入map
            int count=messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            followMap.put("count",count);
            int unreadCount=messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            followMap.put("unreadCount",unreadCount);
        }
        model.addAttribute("followMap",followMap);

        //查询私信和通知总的未读数量
        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount=messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }

    /**
     * 显示通知详情
     * */
    @RequestMapping(value = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Model model,Page page){
        User user = hostHolder.getUser();
        //封装分页信息
        page.setPageSize(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getPageSize());
        //封装数据
        List<Map<String,Object>> noticesList=new ArrayList<>();
        if(notices!=null){
            for(Message notice:notices){
                Map<String,Object> map=new HashMap<>();
                map.put("notice",notice);
                //存通知的内容，包含事件的信息
                Map content = JSONObject.parseObject(HtmlUtils.htmlUnescape(notice.getContent()), map.getClass());
                map.put("user",userService.findUserById((Integer) content.get("userId")));
                map.put("entityType",content.get("entityType"));
                map.put("entityId",content.get("entityId"));
                map.put("postId",content.get("postId"));
                //通知的作者：即系统用户
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticesList.add(map);
            }
        }
        model.addAttribute("noticesList",noticesList);
        //设置已读
        List<Integer> ids=getLetterUnreadIds(notices);
        if(ids!=null && ids.size()>0){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
