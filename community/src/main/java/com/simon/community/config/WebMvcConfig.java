package com.simon.community.config;

import com.simon.community.controller.interceptor.LoginTicketInterceptor;
import com.simon.community.controller.interceptor.MessageInterceptor;
import com.simon.community.service.MessageService;
import com.simon.community.service.UserService;
import com.simon.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhengx
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//    @Autowired
//    private LoginTicketInterceptor loginTicketInterceptor;
//
////    @Autowired
////    private LoginRequiredInterceptor loginRequiredInterceptor;
//
//    @Autowired
//    private MessageInterceptor messageInterceptor;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginTicketInterceptor(hostHolder, userService)).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
//        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(new MessageInterceptor(messageService, hostHolder)).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
