package com.simon.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 用于发邮件的工具类
 */
@Component
@Slf4j(topic = "MailClient")
public class MailClient  {
    //sprint boot只要引入了mail启动器，
    // 创建 JavaMailSenderImpl 实例并注入到 Spring 容器中
    @Autowired
    private  JavaMailSender mailSender;

    //发件人固定，单列属性
    @Value("${spring.mail.username}")
    private  String from;

    public  void sendMail(String to, String subject, String content) {
        //MimeMessege类用于封装邮件的完整内容
        //MimeMessageHelper 是 Spring 框架提供的一个工具类，用于简化 MimeMessage的构建过程。
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //第二个参数 true 表示支持多部分内容（如附件）
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            //设置邮件信息
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //true代表是支持html邮件、默认为false，即只支持文本邮件
            helper.setText(content, true);
            //发送邮件
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件失败"+e.getMessage());
        }

    }
}
