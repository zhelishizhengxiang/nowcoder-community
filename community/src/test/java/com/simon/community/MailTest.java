package com.simon.community;

import com.simon.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    //模拟发送html邮件,获取模板引擎
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("zhengx.simon@foxmail.com","TEST","i love you");
    }

    @Test
    public void testHtmlMail() {
        //给模板传参
        Context context = new Context();
        context.setVariable("username","zhengx");
        //调用模板引擎生成String类型的动态网页
        String str = templateEngine.process("/mail/demo", context);
        System.out.println(str);
        mailClient.sendMail("zhengx.simon@foxmail.com","TEST",str);
    }
}
