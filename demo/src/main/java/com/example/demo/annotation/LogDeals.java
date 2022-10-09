package com.example.demo.annotation;


import java.lang.annotation.*;

/**
 * 日志注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogDeals {
    String value() default "";
}
