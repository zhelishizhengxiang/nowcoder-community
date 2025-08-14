package com.simon.community.service;

import com.simon.community.dao.UserMapper;
import com.simon.community.pojo.User;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zhengx
 * @version 1.0
 */
@Service

public class UserService  implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    //发邮件用的模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //发邮件的工具类
    @Autowired
    private MailClient mailClient;

    //注入项目名和域名用于生成激活码
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 根据id查询用户
     *
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * @return 返回的信息（错误信息或者成功信息）
     * @purpose 注册用户
     *
     */
    public Map registerUser(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //判断注册信息是否符合要求
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //判断邮箱、用户名是否存在
        if (userMapper.selectByName(user.getUsername()) != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "邮箱已存在");
            return map;
        }

        //对密码加密，注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);

        //发送激活邮件，激活用户
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/101/code
        context.setVariable("url", domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode());
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号",content);
        return map;
    }

    /**
     * @purpose 激活用户
     * @return 是否激活成功
     */
    public int activate(int id,String activationCode) {
        User user = userMapper.selectById(id);
        if (user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatus(id,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
}
