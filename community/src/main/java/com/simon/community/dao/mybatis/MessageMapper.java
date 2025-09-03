package com.simon.community.dao.mybatis;

import com.simon.community.pojo.Message;

import java.util.List;

public interface MessageMapper {

    /**
     * 查询当前用户的会话列表，并且针对每个会话只返回最新消息。
     * 系统用户发的消息的from_id为1，为通知
     * */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * */
    int selectConversationsCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * */
    List<Message> selectLetters(String conversationId, int offset,int limit);

    /**
     * 查询某个会话所包含的私信数量
     * */
    int selectLettersCount(String conversationId);

    /**
     * 查询未读私信数量
     * */
     int selectLetterUnreadCount(int userId, String conversationId);


     /**
      * 新增消息
      * */
     int insertMessage(Message message);

     /**
      * 更改多条消息状态
      * */
     int updateStatus(List<Integer> ids,int status);

     /**
      * 某个主题下最新的通知
      * */
     Message selectLatestNotice(int userId,String topic);


     /**
      * 某个主题的包含的通知总数量
      * */
     int selectNoticeCount(int userId,String topic);

     /**
      * 查询未读的通知数量
      * */
     int selectNoticeUnreadCount(int userId,String topic);

     /**
      * 某个主题所包含的通知
      * */
     List<Message> selectNotices(int userId, String topic ,int offset, int limit);
}