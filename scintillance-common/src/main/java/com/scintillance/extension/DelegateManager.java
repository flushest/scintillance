package com.scintillance.extension;

import com.scintillance.annotation.Delegate;
import com.scintillance.annotation.RouteProperty;
import com.scintillance.exception.SciNotSuchDelegateException;
import com.scintillance.util.ClassUtil;
import com.scintillance.util.PriorityComparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2018/2/27 0027.
 * 聚集路由代理构建
 */
@Slf4j
public class DelegateManager {
    private static ConcurrentMap<Class, ConcurrentMap<String, String>> cachedDelegateBeanDefinition = new ConcurrentHashMap<>();
    private static ConcurrentMap<Class, Class> cachedDelegateProxy = new ConcurrentHashMap<>();

    public static void addDelegateBeanDefinition(Class interfaceClass, String key, String beanName) {
        ConcurrentMap<String, String> delegateElements = getDelegateElements(interfaceClass);
        delegateElements.putIfAbsent(key, beanName);
    }

    public static void addDelegateProxy(Class interfaceClass, Class proxyClass) {
        cachedDelegateProxy.putIfAbsent(interfaceClass, proxyClass);
    }

    private static ConcurrentMap<String, String> getDelegateElements(Class interfaceClass) {
        ConcurrentMap<String, String> delegateElements = cachedDelegateBeanDefinition.get(interfaceClass);

        if(delegateElements == null) {
            cachedDelegateBeanDefinition.putIfAbsent(interfaceClass, new ConcurrentHashMap<>());
            delegateElements = cachedDelegateBeanDefinition.get(interfaceClass);
        }
        return delegateElements;
    }

    public static String getBeanName(Class interfaceClass, String key) {
        ConcurrentMap<String, String> delegateElements = getDelegateElements(interfaceClass);
        return delegateElements.get(key);
    }

    public static Class getProxyClass(Class interfaceClass) {
        Class proxyClass = cachedDelegateProxy.get(interfaceClass);
        if(proxyClass == null) {
            throw new SciNotSuchDelegateException("cannot found delegate proxy for " + interfaceClass.getName());
        }
        return proxyClass;
    }





}
