package com.simon.community.config;

import com.simon.community.pojo.LoginTicket;
import com.simon.community.pojo.User;
import com.simon.community.service.UserService;
import com.simon.community.util.CookieUtil;
import com.simon.community.util.HostHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class CustomSecurityFilter extends OncePerRequestFilter {

    //    @Autowired
    private UserService userService;

    //    @Autowired
    private HostHolder hostHolder;

    public CustomSecurityFilter(HostHolder hostHolder, UserService userService) {
        this.hostHolder = hostHolder;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
        throws ServletException, IOException {
        
        // 前置处理逻辑
        long startTime = System.currentTimeMillis();
        System.out.println("开始处理请求: " + request.getRequestURI());
        
        // 获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("当前用户: " + authentication.getName());
        }
        
        try {
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
                    //构建用户认证的结果，存入SecurityContext，以便于Spring security进行授权
                    authentication = new UsernamePasswordAuthenticationToken(user,user.getPassword(),userService.getAuthorities(user.getId()));
//                    SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }else {
                System.out.println("无权限");
//                return;
            }
            filterChain.doFilter(request, response);
        } finally {
            // 后置处理逻辑
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("请求处理完成，耗时: " + duration + "ms");
        }
    }
}