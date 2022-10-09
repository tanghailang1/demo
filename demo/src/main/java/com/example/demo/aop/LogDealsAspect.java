package com.example.demo.aop;

import com.example.demo.annotation.LogDeals;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志切面类
 */
@Slf4j
@Component
@Aspect
public class LogDealsAspect {

    @Pointcut("@annotation(com.example.demo.annotation.LogDeals)")
    public void logDeals(){
    }

    @Around("@annotation(logs)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, LogDeals logs) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取切入点的目标类
        String targetName = proceedingJoinPoint.getTarget().getClass().getName();
        Class<?> targetClass = Class.forName(targetName);
        // 获取切入方法名
        String methodName = proceedingJoinPoint.getSignature().getName();
        String value = logs.value();
        log.info("=======>进入【{}】方法,接口名称为【{}】",methodName,value);
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("=======>方法【{}】执行完毕,接口名称为【{}】,返回结果={},耗时={}ms",methodName,value,result,endTime-startTime);
        return result;
    }



}
