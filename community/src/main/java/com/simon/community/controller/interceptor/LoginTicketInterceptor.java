package com.simon.community.controller.interceptor;

import com.simon.community.pojo.LoginTicket;
import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.CookieUtil;
import com.simon.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 处理是否是登陆状态的拦截器
 */
//@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

//    @Autowired
    private UserService userService;

//    @Autowired
    private HostHolder hostHolder;

    public LoginTicketInterceptor(HostHolder hostHolder, UserService userService) {
        this.hostHolder = hostHolder;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //通过ticket凭证来判断是否处于登录状态
        // 继续过滤器链
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断是否有效以及是否过期
            if(loginTicket.getStatus()==0 &&loginTicket.getExpired().after(new Date())){
                //认为处于登陆的状态，需要向前端返回用户消息
                User user = userService.findUserById(loginTicket.getUserId());
                //CS模式是多对一，客户端每发一个请求，服务器就开启一个线程来处理请求
                //多线程场景下，在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 将user对象存入model中用于显示用户
     * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(hostHolder.getUser()!=null){
            hostHolder.clear();
        }
//        SecurityContextHolder.clearContext();
    }
}
