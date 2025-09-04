package com.simon.community.controller.interceptor;

import com.simon.community.pojo.User;
import com.simon.community.service.DataService;
import com.simon.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author zhengx
 * @version 1.0
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUv(ip);

        //统计去DAU
        //登录才会统计
        User user = hostHolder.getUser();
        if(user!=null){
            dataService.recordDau(user.getId());
        }
        return true;
    }
}
