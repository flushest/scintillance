package com.scintillance.common.orm;

import com.scintillance.common.annotation.TargetDataSource;
import com.scintillance.common.exception.SciNoSuchAnnotationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/3/3 0003.
 * 切换数据源aop
 */
@Aspect
public class DataSourceAspect {

    @Around("@annotation(com.scintillance.common.annotation.TargetDataSource)" +
            "||@within(com.scintillance.common.annotation.TargetDataSource)")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        TargetDataSource targetDataSource = getTargetDataSource(joinPoint);
        DataSourceUtil.setDataSource(targetDataSource.value());
        try {
            joinPoint.proceed();
        } finally {
            DataSourceUtil.clear();
        }
    }

    private TargetDataSource getTargetDataSource(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if(method.isAnnotationPresent(TargetDataSource.class)) {
            return method.getAnnotation(TargetDataSource.class);
        }
        Class declaredClass = method.getDeclaringClass();
        if(declaredClass.isAnnotationPresent(TargetDataSource.class)) {
            return (TargetDataSource) declaredClass.getAnnotation(TargetDataSource.class);
        }
        throw new SciNoSuchAnnotationException("cannot found annotation @TargetDataSource for method " + method.getName() + "in class" + declaredClass.getName());
    }

}
