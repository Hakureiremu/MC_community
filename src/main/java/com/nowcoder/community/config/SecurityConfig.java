//package com.nowcoder.community.config;
//
//import com.nowcoder.community.constant.CommunityConstant;
//import com.nowcoder.community.util.CommunityUtil;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        //忽略静态资源
//        web.ignoring().antMatchers("/resources/**");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        //授权
//        http.authorizeRequests()
//                .antMatchers(
//                        //需要权限管理的路径
//                        "/user/setting",
//                        "/user/upload",
//                        "/discuss/add",
//                        "/comment/add/**",
//                        "/letter/**",
//                        "/notice/**",
//                        "/like",
//                        "/follow",
//                        "/unfollow"
//                )
//                .hasAnyAuthority(
//                        AUTHORITY_USER,
//                        AUTHORITY_ADMIN,
//                        AUTHORITY_MODERATOR
//                )
//                .anyRequest().permitAll()
//                .and().csrf().disable();
//
//        //权限不够时的处理
//        http.exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    //未登录的处理
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                        String xRequestedWith = request.getHeader("x-requested-with");
//                        if("XMLHttpRequest".equals(xRequestedWith)){
//                            //如果是期望返回XML的请求，则为异步请求 （使用AJAX，现基本被json代替）
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录！"));
//                        }else{
//                            //同步请求直接重定向到登录界面
//                            response.sendRedirect(request.getContextPath() + "/login");
//                        }
//                    }
//                })
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                    //权限不足时的处理（已登录）
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                        String xRequestedWith = request.getHeader("x-requested-with");
//                        if("XMLHttpRequest".equals(xRequestedWith)){
//                            //如果是期望返回XML的请求，则为异步请求 （使用AJAX，现基本被json代替）
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限！"));
//                        }else{
//                            //同步请求直接重定向到登录界面
//                            response.sendRedirect(request.getContextPath() + "/denied");
//                        }
//                    }
//                });
//
//        // Security默认会拦截logout请求进行退出处理
//        // 覆盖默认逻辑，把默认的拦截路径改成不处理的无效路径，从而执行自己的退出代码
//        http.logout().logoutUrl("/securitylogout");
//    }
//}
