package com.scintillance.registry;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public interface CandidateComponentPostProcessor {
    void beforeCandidateComponent(ClassPathBeanDefinitionScanner scanner);

    AnnotatedBeanDefinition afterCandidateComponent(AnnotatedBeanDefinition beanDefinition);
}
