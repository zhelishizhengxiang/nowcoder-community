package com.simon.community.controller.advice;

import com.simon.community.util.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhengx
 * @version 1.0
 */
@ControllerAdvice(annotations={Controller.class})
@Slf4j
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public void exception(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常",e.getMessage());
        for(StackTraceElement stackTraceElement : e.getStackTrace()) {
            log.error(stackTraceElement.toString());
        }
        //通过req来判断是普通请求还是异步请求
        String xRequestPath = request.getHeader("s-requested-with");
        //异步请求
        if(xRequestPath.equals("XMLHttpRequest")){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(500,"服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath()+"/error");
        }

    }
}
