package com.scintillance.common.registry;

import com.scintillance.common.annotation.DelegateReference;
import com.scintillance.common.extension.DelegateManager;
import com.scintillance.common.exception.SciException;
import com.scintillance.common.util.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
public class DelegateReferencePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<Field> fields = ClassUtil.getDeclaredFieldsByAnnotation(bean.getClass(), DelegateReference.class);
        for(Field field : fields) {
            Class interfaceClass = field.getType();
            try {
                Object proxyObj = DelegateManager.getProxyObject(interfaceClass);
                field.set(bean, proxyObj);
            } catch (IllegalAccessException e) {
                throw new SciException("IllegalAccessException for " + e.getMessage());
            }
        }
        return bean;
    }


}
