package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    //模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //测试发送纯文本邮件
    @Test
    public void testTextMail(){
        mailClient.sendMail("931967477@qq.com","Test","Welcome.");
    }

    //测试发送html的邮件。设置传给模板变量的值
    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sunday");
        //将content的变量值传给引擎处理，生成我们想要的内容
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        //发送邮件
        mailClient.sendMail("931967477@qq.com","Html",content);
    }
}
