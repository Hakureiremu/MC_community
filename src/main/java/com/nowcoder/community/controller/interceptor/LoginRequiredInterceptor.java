package com.nowcoder.community.controller.interceptor;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //先判断拦截目标，只拦截方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            //从拦截的方法中找出带注解的方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //若方法需要登录才能访问而当前状态未登录,则提示
            if (loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }
}
