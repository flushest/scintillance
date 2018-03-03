package com.scintillance.common.extension;

import com.scintillance.common.exception.SciException;
import com.scintillance.common.exception.SciNoSuchDelegateException;
import lombok.extern.slf4j.Slf4j;

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
    private static ConcurrentMap<Class, Object> cachedDelegateProxyObject = new ConcurrentHashMap<>();

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
            throw new SciNoSuchDelegateException("cannot found delegate proxy for " + interfaceClass.getName());
        }
        return proxyClass;
    }

    public static Object getProxyObject(Class interfaceClass) {
        Class proxyClass = getProxyClass(interfaceClass);

        Object proxyObject = cachedDelegateProxyObject.get(proxyClass);
        if(proxyObject == null) {
            try {
                cachedDelegateProxyObject.putIfAbsent(proxyClass, proxyClass.newInstance());
                proxyObject = cachedDelegateProxyObject.get(proxyClass);
            } catch (IllegalAccessException e) {
                throw new SciException("IllegalAccessException for " + e.getMessage());
            } catch (InstantiationException e) {
                throw new SciException("InstantiationException for " + e.getMessage());
            }
        }
        return proxyObject;
    }





}
