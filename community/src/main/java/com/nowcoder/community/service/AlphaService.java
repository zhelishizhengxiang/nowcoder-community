package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDAO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author zhengx
 * @version 1.0
 */

/**
 * 一般情况喜爱，项目中都是使用单例默认较多，也就是@Scope("singleton")*/

//@Service：标记 业务逻辑层（Service 层）。
@Service
//由于被Spring容器管理的Bean，默认是使用单例，也就是单例模式，
@Scope("singleton")//默认参数为singleton，也就是单例
// 如果希望每次getBean方法获得的都是一个新的实例，那么需要给Bean加如下注解
//@Scope("prototype")//当加上此参数后，每次getBean就会生成新的实例
public class AlphaService {
    //为了方便查看init()方法调用的时刻是否是在构造器之后，那么下面创建一个构造器

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    /**
     该bean不只是有Spring容器来管理，还可以由容器管理他的初始化和销毁
     * */

    //容器管理他的初始化，就是在合适的时候自动的调用该方法，那么只需要给该方法加一个注解
    @PostConstruct
    //该注解用于标记一个方法，此方法会在对象实例化(构造器之后调用)并完成依赖注入之后、投入正常使用之前被自动调用，
    //通常用于执行一些初始化操作。
    public void init(){
        System.out.println("初始化AlphaService");
    }

//    @PreDestroy 注解用于标记一个方法，该方法会在对象被销毁之前自动调用。
//    当对象的生命周期即将结束，比如容器关闭、对象被移除等情况，
//    被 @PreDestroy 注解标记的方法会被执行，通常用于执行资源释放、清理等操作，
//    以确保资源被正确关闭，避免资源泄漏。
    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    /**
     演示实项目中如何去运用依赖注入：
     * 3.业务组件(service)会访问dao来访问数据库*/
    @Autowired
    private AlphaDAO alphaDAO;

    //模拟实现查询业务
    public String find(){
        return alphaDAO.select();
    }
}
