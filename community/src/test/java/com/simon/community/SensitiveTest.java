package com.simon.community;

import com.simon.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter  sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String test="这里可以吸毒，可以嫖娼，可以赌博,可以开票，傻逼，sb";
        System.out.println(sensitiveFilter.filter(test));

        String test1="这里可以ⅇ吸ⅇ毒ⅇ，可以嫖娼，可以赌博,可以开票，傻逼，sb";
        System.out.println(sensitiveFilter.filter(test1));
    }
}
