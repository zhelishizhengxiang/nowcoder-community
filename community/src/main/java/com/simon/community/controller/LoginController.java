package com.simon.community.controller;

import com.google.code.kaptcha.Producer;
import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.RedisKeyUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengx
 * @version 1.0
 */
@Controller
@Slf4j(topic = "LoginController")
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(User user, Model model) {
        Map map = userService.registerUser(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("targetUrl", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/register";
        }
    }


    @RequestMapping(value = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int id,
                             @PathVariable("code") String activationCode) {
        int result = userService.activate(id, activationCode);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "您的账号已经激活成功,可以正常使用了!");
            model.addAttribute("targetUrl", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经被激活");
            model.addAttribute("targetUrl", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("targetUrl", "/index");
        }

        return "site/operate-result";
    }

    /**
     * 获取验证码图片
     * */
    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        //生成的验证码服务端需要记住，并且获取图片和登录是多个请求，所以需要使用session
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码放入session中
        //session.setAttribute("kaptcha", text);

        //验证码的归属,用于识别当前用户
        String kaptchaOwner= CommunityUtil.generateUUID();
        Cookie cookie=new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //验证码存入redis中，并且设置过期时间
        String redisKey= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        //由于输出的是图片，所以需要手动获取输出流进行输出
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            //使用工具类进行发输出
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            log.error("验证码响应失败："+e.getMessage());
        }

    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * @param code 验证码
     * @param username 用户名
     * @param password 密码
     * @param rememberMe 是否点击记住我
     * */
    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String login(String username,String password,String code, boolean rememberMe,Model model,
                        HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner) {
        //判断验证码是否正确
        //String kaptcha=session.getAttribute("kaptcha").toString();

        // 从cookie中取凭证,判断验证码是否正确
        String kaptcha=null;
        //数据未过期
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey=RedisKeyUtil.getKaptchaKey(kaptchaOwner);
             kaptcha=(String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(kaptcha) ||!kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //验证账号密码是否有问题，并且设置登陆状态时间
        int expiredSeconds=rememberMe? REMEMBER_EXPIRED_SECONDS: DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //处理结果
        if(map.containsKey("ticket")) {
            //成功,发送登录凭证作为cookie
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            //失败,显示错误信息,并回显数据
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }


    @RequestMapping(value = "/logout" ,method =  RequestMethod.GET)
    public String logout(@CookieValue("ticket")  String ticket) {
        //通过cookie获得凭证ticket
        userService.logout(ticket);
        //重定向默认get方法
        return "redirect:/login";
    }
}
