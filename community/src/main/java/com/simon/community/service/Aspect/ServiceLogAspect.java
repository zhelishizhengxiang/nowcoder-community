package com.simon.community.service.Aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhengx
 * @version 1.0
 */
//@Component
//@Aspect
@Slf4j
public class ServiceLogAspect {

    @Pointcut("execution(* com.simon.community.service.*.*(..) )")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        //用户[ip地址]在[时间],访问了[什么方法]

        //获取请求得上下文信息
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //如果不是同各国controller调用切面就不去记录日志
        if (requestAttributes == null) {
            return ;
        }
        //获取请求对象
        HttpServletRequest request = requestAttributes.getRequest();
        //获取ip地址
        String ipAddr = request.getRemoteHost();
        String now=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取目标方法得全限定类名
        String className = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        log.info(String.format("用户[%s]在[%s]访问了[%s]", ipAddr, now, className));
    }
}
