package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author 008
 * @create 2023-07-17 10:04
 */
@Component
public class MailClient {
    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            //设置邮件信息
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);//true表示支持html的文本
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败"+e.getMessage());
        }

    }
}
