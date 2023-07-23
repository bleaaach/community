package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发送异常："+e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            log.error(element.toString());
        }

        //判断是主动请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        //异步请求
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            //确保响应的内容以纯文本形式传输，并且使用UTF-8编码。

            PrintWriter writer=response.getWriter();

            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
            //获取一个JSON格式的字符串，并将该字符串写入响应流中。这样可以将一个包含错误信息的JSON字符串作为响应返回给客户端。
        }else{
            //主动请求
            response.sendRedirect(request.getContextPath()+"/error");
        }

    }

}
