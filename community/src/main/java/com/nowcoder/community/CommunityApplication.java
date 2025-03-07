package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication 一般用于程序的入口用该注解。其是以下三个核心注解的 ​组合：
//@Configuration标记该类为 配置类，允许通过 @Bean 注解显式定义 Bean。
//@EnableAutoConfiguration：启用 Spring Boot 的 ​自动配置​ 机制，根据类路径中的依赖（如 JAR 包）自动配置应用组件（如数据源、Web 服务器等）。
//@ComponentScan 自动扫描当前包及其子包下的组件（如 @Component、@Service、@Controller 等注解的类），并将其注册为 Bean。
//注意扫描范围是当前文件所在的包及其子包
@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		//是 Spring Boot 应用的 入口方法，它的作用是从 main 方法启动整个 Spring Boot 应用
		//该方法不只是启动了tomcat服务器，还自动的帮我们创建了spring容器，Spring容器的管理对象称为Bean
		//具体为创建并初始化 Spring 容器（ApplicationContext)，扫描并注册所有 @Component、@Service、@Controller 等注解标记的 Bean
		SpringApplication.run(CommunityApplication.class, args);
	}

}
