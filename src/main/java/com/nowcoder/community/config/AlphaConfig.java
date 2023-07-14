package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author shkstart
 * @create 2023-07-14 10:06
 */
@Configuration
public class AlphaConfig {
    //这个方法的作用就是将返回的对象装配到容器里
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
