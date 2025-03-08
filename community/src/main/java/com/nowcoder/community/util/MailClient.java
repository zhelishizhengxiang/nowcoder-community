package com.nowcoder.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;



//把发邮件的事情委托给新浪邮箱来做
@Component //标记为通用bin
public class MailClient {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired //JavaMailSender该核心组件是由spring容器去管理
    private JavaMailSender mailSender;

    //发件人是固定的，直接注入
    @Value("${spring.mail.username}")
    private String from;

    /**
     * @usage 发送邮件
     * @param to 发送目标
     * @param content 邮件内容
     * @param subject 邮件主体
     * */
    public void sendMail(String to, String subject, String content) {
        try {
            //JavaMailSender里面的方法1为创建MimeMessage对象，为邮件的主体
            //send就是把邮件发出去的方法。spring提供了工具类 MimeMessageHelper用于帮助构建 MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //设置发件人收件人主体和内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //不加第二个参数的话，默认为普通文本，写true代表支持html文本
            helper.setText(content, true);
            //调用send方法发送出去
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败:" + e.getMessage());//记录日志
        }
    }

}
