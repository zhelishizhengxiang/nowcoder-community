package com.simon.community.controller;

import com.google.code.kaptcha.Producer;
import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
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
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成的验证码服务端需要记住，并且获取图片和登录是多个请求，所以需要使用session
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码放入session中
        session.setAttribute("kaptcha", text);
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


}
