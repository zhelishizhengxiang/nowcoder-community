package org.simom.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {

    //记录spring容器，将其作为属性方便测试
    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        //测试使用spring容器
        SimpleDateFormat format = applicationContext.getBean("simpleDateFormat", SimpleDateFormat.class);
        System.out.println(format.format(new Date()));
    }


}
