package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author zhengx
 * @version 1.0
 */
//会使类被标记为Bean的四个注解：为@Component以及其派生注解
//@Component： 通用注解，标记任何需要由 Spring 管理的组件。//派生注解（语义化注解，功能与 @Component 相同，但用于不同层）：
//@Controller：标记 ​Web 控制器层​（如 MVC 或 RESTful API）。
//@Service：标记 ​业务逻辑层​（Service 层）。
//@Repository：标记 ​数据访问层​（DAO 或 Repository 层），并自动处理数据库异常。
//@Configuration：标记 ​配置类，用于定义 @Bean 方法。

//在配置类（@Configuration 注解的类）中，通过 @Bean 注解的方法返回的对象也会成为 Bean。
@Controller
//@RequestMapping注解的核心作用是将一个 HTTP 请求的 URL 映射到对应的控制器类或方法上，使得当客户端发送特定 URL 的请求时，Spring 能够找到并执行相应的处理方法。
@RequestMapping("/alpha")//相当于给这个类取一个访问的名字,浏览器通过这个名字来访问这个类以及这个类中的方法
public class AlphaController {
    @RequestMapping("/hello")
    //加次注解防止实景retrun的语句从网址变为字符串
    @ResponseBody
    public  String sayHello(){
        return "Hello Spring Boot.";
    }

    /**
     * 下面演示真实项目中如何去运用依赖注入：
     * 1.由controller来处理浏览器的请求，
     * 2.在处理请求的过程中，会调用业务组件（service）去处理当前的业务
     * 3.业务组件会访问dao来访问数据库*/

//    2.在处理请求的过程中，会调用业务组件（service）去处理当前的业务
    @Autowired
    private AlphaService alphaService;

    //模拟处理当前业务（查询请求）
    @RequestMapping("/data")
    //加次注解防止实景retrun的语句从网址变为字符串
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }


    /**SPringMVC解决的是视图层的问题，而视图层要写的第一个就是Controller，
     * 其会组织数据将其封装在Model里。之后会交给Thymeleaf去对html网页做事情
     * 另外还需要写模板引擎Thymeleaf所需要的模板，其会写在resource下的template中
     * 下面就演示模板引擎Thymeleaf的语法，所以接下来就用一些例子来讲明
     * */

    /*首先演示在SpringMVC框架下，如何获得请求对象和响应对象（里面分别放着请求数据和相应数据）
      这是比较底层的行为。实际上SpringMVC对其做了一些封装，有更简便的方式。
      此处先讲第一个，用于理解一些比较底层的机制
    */

    /**  如果想要获取请求对象和响应对象，只需要再这个方法上加以声明就可以。
     你声明了这个两个类型后，前端控制器DispatcherServlet在调该方法的时候，就自动会把这两个对象传给你（相当于底层就帮你创建好了）
     */
    //该方法不写返回类型是因为我们可以通过Response对象，可以像浏览器输出任何数据，不依赖返回值

    /**
     * request请求对象常用的类型(接口)是HttpServletRequest
     * response相应对象常用的类型为
     * HttpServletResponse*/
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //利用请求对象去处理请求，也就是读取请求当中所包含的数据
        // 获取请求数据（有各种各样的数据，包括请求方式、请求路径、请求行等）
        /**一个请求数据包括很多部分，请求方式和请求路径是请求数据的首行的东西。
         * 后面是请求的消息头，也就是请求行，是若干行的数据
         * 还有请求体，包含的业务数据，也就是参数等
         * */
        System.out.println(request.getMethod());//获取请求方式
        System.out.println(request.getServletPath());//获取请求的路径
        //请求行有很多数据，是k-v结构。
        //此处getHeaderNames得到的是所有请求行的key，此处得到的是一个很老的迭代器Enumeration，
        // 功能和其实和Iterator一样
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            //取到当前的请求行的key，也就是请求行的名字
            String name = enumeration.nextElement();
            //通过请求行的key获得对应的value
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        //获得请求的参数（请求体），code为参数名
        System.out.println(request.getParameter("code"));

        // 返回响应数据。response是用来对浏览器作出响应的对象，也就是给浏览器返回响应数据的对象
        //利用reponse对象去设置返回的数据的类型，下例是设置返回一个网页类型的文本，并且设置字符集
        response.setContentType("text/html;charset=utf-8");
        try (
                //通过reponse获取输出流PrintWriter对象，用于输出东西
                PrintWriter writer = response.getWriter();
        ) {
            //利用输出流PrintWriter对象向浏览器输出一个网页
            /**只输出一个标题肯定不是一个完整的网页，在其之前还要输出head、body等标签，
             * 这些都是利用write方法一个一个写出来的，此处金输出一个标题做例子*/
            writer.write("<h1>牛客网</h1>");//输出了一级标题
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**上面直接利用底层的对象去做这样的事情是十分麻烦的，此处仅作为了解底层机制的例子。
         * 真正使用的都是已经封装好了的，其底层就是干了上面的事情。*/
    }


    /**
     * *****下面来演示封装之后的更简便的处理请求和处理相应的方式****
     *  这分两方面：一个是接受请求的数据基于request；一个是向浏览器返回相应数据，基于response
     *  接下来分开演示
     * */

    /**先演示接收请求的数据用更简便的方式怎么去处理*/


    /*GET（这是请求方式）请求的处理方式，一般用于向服务器获取某些数据时是get请求，一般默认是请求方式就是Get方式*/

    //假设想要查询所有的学生
    // /students?current=1&limit=20
    //此处是希望分页显示，所以需要带一些参数（条件），current=1告诉当前是第一页；limit=20是每一页最多显示数据的条数

    //@RequestMapping中很多参数，不只是路径，下例中是制定的访问路径和method是声明请求的方式
    @RequestMapping(path = "/students", method = RequestMethod.GET)//加了第二个参数后，就只能处理Get请求
    @ResponseBody
    //方法中对应的带这两个参数，就可以获取请求当中的参数值
    public String getStudents(
//            @RequestParam注解，主要用于从 HTTP 请求的参数中获取数据，并将其绑定到控制器方法的参数上。
            //以第一个参数为例讲一讲是在干什么：name = "current"代表请求request中的名为current的值给后面对应的参数；
            //required = false代表传不传该参数都行； defaultValue = "1"代表默认值
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    /**还有一个GET请求比较常见的方式，具体如下
     * 比如说要根据id来查询一个学生而不是所有学生*/
    // /student/123，等价于/student?id=123，这是直接让参数成为了路径的一部分
    // 此时就是另一种方法去获取
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    //@PathVariable主要用于从 URL 的路径部分获取变量值，并将其绑定到控制器方法的参数上
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    /**总结：在GET请求中，有两种传参方式，
     * 一种是问号拼接students?current=1&limit=20
     * 一种是把参数放到路径之中/student/123
     * */

    //注：浏览器要想向服务器提交数据，浏览器必须要打开一个表单的网页，
    // 要通过表单填写数据，才能够将数据提交给服务器。
    // 接下来现在static创建一个静态网页，动态的网页放在template之下
    // POST请求：用于浏览器向服务器提交数据的请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    //获取post请求参数的方式：在方法中直接声明参数，与表单中数据的name一致，就会自动传过来
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
    /**
     * 上面是request请求的处理方式的演示，接下来演示如何向浏览器返回响应数据
     * 之前都演示的是相应的是字符串，接下来演示如何响应一个动态的html和json请求
     * */

    // 响应HTML数据
    //假设浏览器要查询一个老师，服务器查询到数据要把数据返回给浏览器
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    /**ModelAndView主要用于封装控制器处理请求后返回的模型数据和视图信息。
     * 它允许控制器在处理请求后，既可以携带要展示给用户的数据（模型数据），
     * 又可以指定要渲染的视图（如 JSP、Thymeleaf 模板等）*/
    public ModelAndView getTeacher() {
//        先实例化一个ModelAndView
        ModelAndView mav = new ModelAndView();
        // 添加模型数据，参数名和参数值
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        // 设置要渲染的视图的路径名称：动态的html要放在templates之下，
        // 并且templates不用写，只写后面的路径即可，默认模板就是html文件，所以不用写后缀名
        mav.setViewName("/demo/view");
        return mav;
    }

    /**另一种相应服务器请求的方式，返回html的方式如下，*/
    //假设是查询学校
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    //其中参数是一个MOdel，该参数并不是我们自己创建，而是DispatcherServlet在调该方法时，
    // 识别到该参数，会自动实例化一个对象然后传给该参数
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";//return的就是视图的路径也就是html的路径
    }


    /**JSON请求是一种在客户端和服务器之间进行数据交互的常见方式，
     * 它以 JSON 格式来组织和传输数据。
     *
     * JSON 请求就是客户端向服务器发送包含 JSON 数据的请求，
     * 服务器接收到请求后对 JSON 数据进行解析和处理，然后可能返回另一个 JSON 格式的响应。*/
    // 响应JSON数据(异步请求)：当前网页不刷新，但是会自动访问防腐漆得到结果
    // Java对象 -> JSON字符串 -> JS对象：
    // 将java对象的数据返回给浏览器，浏览器解析对象用的是面向对象语言JS，
    // 二者不兼容，可以通过JSon字符串来做到对二者的兼容

    //假设查询一个员工
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    //向浏览器返回Json字符串，那么就需要返回字符串所以用下面的注解
    @ResponseBody
    //DispatcherServlet调该方法时看到上面的注解，就会自动的返回的类型转成JSOn字符串发送给浏览器
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    //假设返回的数据是一组员工，即多个相似结构
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    //用list去装员工
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(emp);

        return list;
    }





}
