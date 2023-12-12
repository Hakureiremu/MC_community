package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//是否需要登录才能访问的方法
@Target(ElementType.METHOD)
//程序运行时注解有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
