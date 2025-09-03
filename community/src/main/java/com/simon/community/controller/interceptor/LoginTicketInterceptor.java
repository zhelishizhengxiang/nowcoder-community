package com.simon.community.controller.interceptor;

import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
