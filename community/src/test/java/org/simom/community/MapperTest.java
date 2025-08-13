package org.simom.community;

import com.simon.community.CommunityApplication;
import com.simon.community.dao.DiscussPostMapper;
import com.simon.community.dao.UserMapper;
import com.simon.community.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

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
}
