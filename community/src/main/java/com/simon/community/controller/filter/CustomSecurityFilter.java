package com.simon.community.controller.filter;

import com.simon.community.pojo.LoginTicket;
import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
@Component
public class CustomSecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;



    /**
     * 在spring security认证前执行此逻辑将用户信息加入
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
        throws ServletException, IOException {

        // 继续过滤器链
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断是否有效以及是否过期
            if(loginTicket.getStatus()==0 &&loginTicket.getExpired().after(new Date())){
                //认为处于登陆的状态，需要向前端返回用户消息
                User user = userService.findUserById(loginTicket.getUserId());
                //构建用户认证的结果，存入SecurityContext，以便于Spring security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(user,user.getPassword(),userService.getAuthorities(user.getId()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}