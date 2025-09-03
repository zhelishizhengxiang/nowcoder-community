package com.simon.community.service;

import com.simon.community.dao.mybatis.LoginTicketMapper;
import com.simon.community.dao.mybatis.UserMapper;
import com.simon.community.pojo.LoginTicket;
import com.simon.community.pojo.User;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.MailClient;
import com.simon.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据id查询用户（双检加锁）
     */
    public User findUserById(int id) {
        User user=null;
        //第一次检查
        user=getCache(id);
        if(user==null){
            synchronized (UserService.class) {
                //第二次检查
                user=getCache(id);
                if(user==null){
                    //从数据库查并回写缓存
                    user= initCache(id);
                }
            }
        }
        return user;
    }

    /**
     * 根据name查询用户
     * */
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 注册用户
     * @return 返回的信息（错误信息或者成功信息）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> registerUser(User user) {
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
     * 激活用户
     * @return 是否激活成功
     */
    @Transactional(rollbackFor = Exception.class)
    public int activate(int id,String activationCode) {
        User user = userMapper.selectById(id);
        if (user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatus(id,1);
            //清理缓存
            clearCache(id);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }


    /**
     * 用户登录,验证码在controller判断，因为controller可以拿到session
     * @param password 密码（未加密）
     * @param expiredSecond  凭证过期时间
     * */
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> login(String username,String password,int expiredSecond) {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //验证激活状态
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //登陆成功，生成登录凭证，维持登录状态
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtil.generateUUID());
        ticket.setExpired(new Date(System.currentTimeMillis() + expiredSecond*1000));
//        loginTicketMapper.insertLoginTicket(ticket);

        //存放到redis当中
        String redisKey= RedisKeyUtil.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(redisKey,ticket);

        //需要把凭证返回给客户端，客户端需要留存
        map.put("ticket",ticket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket 传过来的凭证
     * */
    @Transactional(rollbackFor = Exception.class)
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket,1);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginticket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginticket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginticket);
    }


    /**
     * 查询凭证
     * */
    public LoginTicket findLoginTicket(String ticket){
        String  redisKey= RedisKeyUtil.getTicketKey(ticket);
        return  (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 修改用户头像
     * */
    @Transactional(rollbackFor = Exception.class)
    public int updateHeaderUrl(String headerUrl,int id) {
        int rows = userMapper.updateHeaderUrl(id, headerUrl);
        clearCache(id);
        return  rows;
    }

    /**
     * 修改用户密码
     * @param password 密码未加密
     * @param user  需要改密码的用户
     * */
    @Transactional(rollbackFor = Exception.class)
    public int updatePassword(User user,String password) {
        //传进来的密码未被加密，所以需要获取加密后的密码
        password=CommunityUtil.md5(password+user.getSalt()).toString();
        int rows =userMapper.updatePassword(user.getId(),password);
        clearCache(user.getId());
        return rows;

    }

    /**
     * redis和mysql的双写一致性做法：
     * 1.读时采用双检枷锁策略
     * 2.修改时:先更新数据库再更新缓存
     * */

    /**
     * 从缓存中获取用户信息
     * */
    private User getCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        return  (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 缓存中取不到时，就从数据库查询之后初始化缓存
     * */
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 用户信息变更时，清除缓存信息
     * */
    private void clearCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    /**
     * 根据用户获取用户的权限
     * */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = userMapper.selectById(userId);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return  authorities;
    }




}
