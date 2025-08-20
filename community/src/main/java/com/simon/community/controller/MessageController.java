package com.simon.community.controller;

import com.simon.community.pojo.Message;
import com.simon.community.pojo.Page;
import com.simon.community.pojo.User;
import com.simon.community.service.MessageService;
import com.simon.community.service.UserService;
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
@RequestMapping("/letter")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 私信列表
     * */
    @RequestMapping(value ="/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        page.setPageSize(5);
        page.setPath("/letter/list");
        User user = hostHolder.getUser();
        page.setRows(messageService.findConversationsCount(user.getId()));
        //会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getPageSize());
        //总的未读的消息数量
        int unreadCountAll = messageService.findLetterUnreadCount(user.getId(), null);
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
        model.addAttribute("unreadCountAll",unreadCountAll);
        return "/site/letter";
    }

    /**
     * 私信内容
     * */
    @RequestMapping(value = "/detail/{conversationId}",method = RequestMethod.GET)
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
                if(letter.getStatus()==0 && letter.getToId()==hostHolder.getUser().getId()){
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
    @RequestMapping(value = "/send",method = RequestMethod.POST)
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
}
