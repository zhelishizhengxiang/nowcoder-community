package com.simon.community;

import com.simon.community.dao.mybatis.DiscussPostMapper;
import com.simon.community.dao.mybatis.LoginTicketMapper;
import com.simon.community.dao.mybatis.MessageMapper;
import com.simon.community.dao.mybatis.UserMapper;
import com.simon.community.pojo.LoginTicket;
import com.simon.community.pojo.Message;
import com.simon.community.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author zhengx
 * @version 1.0
 */
@Slf4j
@SpringBootTest(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelect(){
        System.out.println(userMapper.selectById(101));
        System.out.println(userMapper.selectByEmail("nowcoder23@sina.com"));
        System.out.println(userMapper.selectByName("liubei"));
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setUsername("shamohe");
        user.setPassword("123456");
        userMapper.insert(user);
    }

    @Test
    public void testUpdate(){
        System.out.println(userMapper.updateStatus(1,0));
    }

    @Test
    public void testSelectPosts(){
        discussPostMapper.selectDiscussPosts(0, 0, 10).forEach(System.out::println);
        discussPostMapper.selectDiscussPosts(0,10,10).forEach(System.out::println);
        System.out.println(discussPostMapper.selectDiscussPostsCount(0));

        discussPostMapper.selectDiscussPosts(149, 0, 10).forEach(System.out::println);
        System.out.println(discussPostMapper.selectDiscussPostsCount(149));
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("123456");
        loginTicket.setUserId(50);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket ticket = loginTicketMapper.selectByTicket("123456");
        if(ticket!=null){
            loginTicketMapper.updateStatus(ticket.getTicket(),1);
        }
        LoginTicket ticket1 = loginTicketMapper.selectByTicket("123456");
        System.out.println(ticket1.getStatus());
    }

    @Test
    public void testConversations(){
        List<Message> list=messageMapper.selectConversations(111,0,20);
        for (Message  msg:list){
            System.out.println(msg);
        }


        System.out.println(messageMapper.selectConversationsCount(111));
        List<Message> list1=messageMapper.selectLetters("111_112",0,20);
        for (Message  msg:list1){
            System.out.println(msg);
        }

        System.out.println(messageMapper.selectLettersCount("111_112"));

        System.out.println(messageMapper.selectLetterUnreadCount(131,"111_131"));
    }

    @Test
    public void testReadMessage(){
        messageMapper.updateStatus(Arrays.asList(355,356,357),1);
    }

}
