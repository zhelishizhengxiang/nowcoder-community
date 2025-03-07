package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author zhengx
 * @version 1.0
 */
///只要运行程序，Spring容器会自动扫描这个Bean并将其装配到容器
//@Repository：标记 ​数据访问层​（DAO 或 Repository 层），并自动处理数据库异常。
@Repository
//如果通过getBean方法传入参数AlphaDAO.class,该类型有两个Bean在Spring容器中，这样挑选会报错，
//为了能够挑选出我希望的类别来，只需要在我希望的类别上添加以下注解
@Primary
public class AlphaDAOMyBatisImpl implements AlphaDAO {
    @Override
    public String select() {
        return "MyBatis";
    }
}
