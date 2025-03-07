package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author zhengx
 * @version 1.0
 */
//加了Repository，只要运行程序，Spring容器会自动扫描这个组件并将其装配到容器

//每个Bean都是有名字的，默认是类名并且首字母小写。
// 如果想要自定义名字，那么就在@Repository后面加上(),里面写的字符串就是Bean的名字
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements  AlphaDAO{
    @Override
    public String select() {
        return "Hibernate";
    }
}
