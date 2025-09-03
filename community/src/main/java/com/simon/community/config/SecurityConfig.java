package com.simon.community.config;

import com.simon.community.service.UserService;
import com.simon.community.util.CommunityConstant;
import com.simon.community.util.CommunityUtil;
import com.simon.community.util.HostHolder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhengx
 * @version 1.0
 */
@Configuration
public class SecurityConfig  implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    // 配置忽略特定路径的安全检查，比如静态资源路径
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resources/**");
    }

    // 配置安全过滤链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/user/setting",
                                 "/user/upload",
                                 "/discussPost/add",
                                 "/comment/add/**",
                                 "/letter/**",
                                 "/notice/**",
                                 "/like",
                                 "/follow",
                                 "/unfollow").authenticated()
                         .anyRequest().permitAll()//除了上面设置的地址需要登录访问,其它所有的请求地址可与直接访问
                 )
                 .formLogin(form -> form.disable()) // 关闭默认表单登录
                 .addFilterBefore(new CustomSecurityFilter(hostHolder, userService),
                         UsernamePasswordAuthenticationFilter.class)

//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/public/**", "/community/login").permitAll()
//                         .anyRequest().authenticated()
//                 )
//             .httpBasic(basic -> basic.disable()) // 关闭 HTTP Basic 认证
            // 配置授权规则

//            // 开启CSRF防护（默认开启，可按需配置）
//                 .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

        //注册自定义的处理器(未认证用户访问需要认证资源的处理器)
            .exceptionHandling(exceptionHandling->{
                exceptionHandling.authenticationEntryPoint(new  AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        //未登录时的处理
                        String xRequestPath = request.getHeader("X-requested-with");
                        //根据是普通请求还是异步请求做不同处理
                        if("XMLHttpRequest".equals(xRequestPath)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            out.write(CommunityUtil.getJSONString(403,"您还未登录哦！"));
                            return;
                        }
                        //同步就直接重定向到登录页面
                        response.sendRedirect(request.getContextPath()+"/login");
                    }
                });
            })

        //注册自定义的处理器(认证后的用户访问需要认证资源时因为权限不足走的处理器)
            .exceptionHandling(exceptionHandling->{
                exceptionHandling.accessDeniedHandler(new  AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        //权限不足时的处理
                        String xRequestPath = request.getHeader("X-requested-with");
                        //根据是普通请求还是异步请求做不同处理
                        if("XMLHttpRequest".equals(xRequestPath)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            out.write(CommunityUtil.getJSONString(403,"您没有访问此功能的权限！"));
                            return;
                        }
                        //同步就直接重定向到登录页面
                        response.sendRedirect(request.getContextPath()+"/denied");
                    }
                });
            });
//
//        //Security底层会默认拦截/logout请求，进行退出处理
//        //覆盖默认逻辑，这样才能使用自己的退出代码。
//        // 让它拦截一个我们用不到的路径即可
        http.logout(logout->logout.logoutUrl("/securityLogout"));

        //禁用csrf
        http.csrf(csrf->csrf.disable());
//        // 保证 SecurityContext 自动保存到 Session
//        http.securityContext(securityContext ->
//                securityContext.securityContextRepository(new HttpSessionSecurityContextRepository())
//                        .requireExplicitSave(false)
//        );

         return http.build();
    }

}
