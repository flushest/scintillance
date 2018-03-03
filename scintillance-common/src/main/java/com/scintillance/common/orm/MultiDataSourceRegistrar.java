package com.scintillance.common.orm;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
public class MultiDataSourceRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        beanDefinitionRegistry.registerBeanDefinition("dataSource", new AnnotatedGenericBeanDefinition(DynamicDataSource.class));
        beanDefinitionRegistry.registerBeanDefinition("multiDataSourceRegistrar", new AnnotatedGenericBeanDefinition(MultiDataSourceRegistrar.class));
    }
}
