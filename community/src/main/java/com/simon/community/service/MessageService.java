package com.simon.community.service;

import com.simon.community.dao.mybatis.MessageMapper;
import com.simon.community.pojo.Message;
import com.simon.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter  sensitiveFilter;
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int  findConversationsCount(int userId){
        return messageMapper.selectConversationsCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int findLettersCount(String conversationId){
        return messageMapper.selectLettersCount(conversationId);
    }

    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    /**
     * 新增一条消息
     * */
    public int addMessage(Message message){
        //过滤敏感词和标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 将多条消息变为已读
     * */
    public  int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }


    /**
     * 查询某个主题最新的通知
     * */
    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId,topic);
    }

    /**
     * 查询某个主题的通知总数
     * */
    public int findNoticeCount(int userId,String topic){
        return messageMapper.selectNoticeCount(userId,topic);
    }

    /**
     * 查询未读的通知数量
     * */
    public int findNoticeUnreadCount(int userId,String topic){
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }

    /**
     * 分页查询通知
     * */
    public List<Message> findNotices(int userId,String  topic,int offset,int limit){
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }



}
