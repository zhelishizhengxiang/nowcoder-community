package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDAO;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 在测试类里演示IOC Inversion of Control, 控制反转。的使用方式，
 * */

@RunWith(SpringRunner.class)
//测试用的配置类希望用的和正式的配置类一样，那么就可以用下面的注解@ContextConfiguration
@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)

/**控制反转就是把创建和管理 bean 的过程转移给了第三方。
 * 而这个第三方，就是 Spring IoC Container，
 * 对于 IoC 来说，最重要的就是容器。*/


/**容器负责创建、配置和管理 bean，
 * 也就是它管理着 bean 的生命，控制着 bean 的依赖注入。*/
//控制反转（IoC, Inversion of Control）的核心是Spring容器，那么得到容器的方法就为实现ApplicationContextAware接口
// ApplicationContextAware 接口​ 的作用是 ​让一个 Bean 获取到 Spring 容器的引用（ApplicationContext
class CommunityApplicationTests implements ApplicationContextAware {

	//加一个成员便来那个，用于记录容器
	private ApplicationContext applicationContext;


	//此方法中的参数类型ApplicationContext其实就是一个容器。
	// 如果一个类实现了ApplicationContextAware并实现了setApplicationContext
	//那么Spring容器会检测到，Spring容器在扫描当前包及其子包下的组件的时候，
	//Spring 容器会在初始化该 Bean 时自动调用 setApplicationContext 方法，
	//将当前的 ApplicationContext 对象引用传递给它。这样，Bean 就可以直接访问 Spring 容器的功能。
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}

	//获得Spring容器的对象引用之后，就可以对其进行使用,也可以管理bean
	@Test
	 public void testApplicationContext() {
		System.out.println(applicationContext);

		//从容器中获取自动装配的Bean,方法参数指的是要获取该类型的Bean
		AlphaDAO alphaDAO=applicationContext.getBean(AlphaDAO.class);
		System.out.println(alphaDAO.select());

		//可以通过给Bean自定义名字，可以通入传入名字来返回这个Bean
		//但是通过这种方式获得，不知道是什么类型，所以此方法不知道返回什么类型就返回Object类型
//		alphaDAO=applicationContext.getBean("alphaHibernate");
		//那么就可以再传入一个类型，代表将得到的Object对象再转型成该类型
		alphaDAO=applicationContext.getBean("alphaHibernate",AlphaDAO.class);
		System.out.println(alphaDAO.select());
	}

	//测试一下Bean的管理方式(实例化、初始化、销毁)
	@Test
	public void testBeanManagement(){
		AlphaService alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);//com.nowcoder.community.service.AlphaService@1c1fa494

		//运行方法可以看出两个getBean方法得到的Bean对象引用的toString方法打印出来的hashcode是一样
		 alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);//com.nowcoder.community.service.AlphaService@1c1fa494

		/**
		 * 根据上述结果可以得到结论：被Spring容器管理的Bean，默认是使用单例，也就是单例模式
		 * */

		/**
		 * 当使用prototype修饰Scope注解时，就会创建多个实例，这样就不是启动Spring boot应用启动时实例化初始化，
		 * 而是在具体getBean的时候再实例化和初始化
		 * */
	}

	//测试一下能否取到第三方Bean——SimpleDateFormat
	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	/**
	 *上面的方法都是主动获取容器，之后拿一个bean来用来达到IOC的目的
	 * 实际上，更多的使用方式是DI——Dependency Injection，而不是主动获取
	 * IOc的实现方式是依赖注入（DI）
	 * */

	/*下面演示依赖注入去实现IOC*/

	//假如当前的Bean要使用AlphaDao,没有必要通过容器去getBean去获取，
	//只需要声明：我要给当前的Bean注入alphaDao就行，注入的话要用一个注解具体如下
	@Autowired//该注解表示：希望Spring容器能够将AlphaDao注入给属性alphaDAO
	/**
	 * @Autowired 注解用于自动装配依赖项，它可以加在成员变量、构造函数、方法等前面
	 * 通常都是加在属性上，这样更简洁更方便
	*/
	//如果希望不能按照设置好的优先级去注入，而是希望注入制定的Bean，就需要加下面的注释
	//而是用于在 Spring 容器中存在多个相同类型的 Bean 时，明确指定要注入哪个具体的 Bean 到目标属性中
	@Qualifier("alphaHibernate")
	private AlphaDAO alphaDAO;//并不用使实例化，而是使用@AutoWired将AlphaDao注入给属性alphaDAO，这样就可以直接使用

	//同理，想要获取AlphaService也可以这么做,SimpleDate
	@Autowired
	private  AlphaService alphaService;

	@Autowired
	private  SimpleDateFormat simpleDateFormat;

	@Test
	public  void testDI(){
		System.out.println(alphaDAO);//此时并不报错
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}





}
