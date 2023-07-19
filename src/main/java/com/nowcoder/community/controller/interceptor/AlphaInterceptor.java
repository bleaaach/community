package com.nowcoder.community.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@Component
public class AlphaInterceptor implements HandlerInterceptor {

    //在Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("prehandle:"+handler.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    //在Controller之后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("postHandle"+handler.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    //在templateEngine之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("aftertHandle"+handler.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
