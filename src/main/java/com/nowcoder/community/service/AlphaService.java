package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author shkstart
 * @create 2023-07-14 9:47
 */
@Service
@Scope("prototype")
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    //构造时调用
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    //构造后调用
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    //销毁前调用
    @PreDestroy
    public void destory(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
