package com.simon.community.controller.interceptor;

import com.simon.community.annotation.LoginRequired;
import com.simon.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 判断访问的页面是否需要强制登录才能访问的拦截器，如果需要则跳转先去登录
 */
@Deprecated
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    //通过前一个拦截器的user信息来进行判断
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //由于会拦截所有请求，所以先判断是否拦截的是处理器方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //判断方法上是否有注解并且需要强制登录
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getDeclaredAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser()==null){
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
