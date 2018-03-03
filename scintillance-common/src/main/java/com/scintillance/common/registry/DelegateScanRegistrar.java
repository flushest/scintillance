package com.scintillance.common.registry;

import com.scintillance.common.annotation.DelegateScan;
import com.scintillance.common.util.StringUtil;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DelegateScanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);
        SciClassPathBeanDefinitionScanner scanner = new SciClassPathBeanDefinitionScanner(beanDefinitionRegistry, new DelegateCandidateComponentPostProcessor(), new DelegateElementCandidateComponentPostProcessor());
        scanner.scan(packagesToScan.toArray(new String[packagesToScan.size()]));
        registerBeanDefinition(DelegateReferencePostProcessor.class, beanDefinitionRegistry);

    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(DelegateScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] value = attributes.getStringArray("value");
        // Appends value array attributes
        Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty()) {
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }

    private void registerBeanDefinition(Class beanClass, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(DelegateReferencePostProcessor.class);
        beanDefinitionRegistry.registerBeanDefinition(StringUtil.lowerCaseInitial(beanClass.getSimpleName()), beanDefinition);
    }
}
