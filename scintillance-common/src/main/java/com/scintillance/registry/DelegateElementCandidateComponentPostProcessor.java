package com.scintillance.registry;

import com.scintillance.annotation.Delegate;
import com.scintillance.annotation.DelegateElement;
import com.scintillance.exception.SciException;
import com.scintillance.extension.DelegateManager;
import com.scintillance.util.StringUtil;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * Created by Administrator on 2018/3/1 0001.
 */
public class DelegateElementCandidateComponentPostProcessor implements CandidateComponentPostProcessor {
    @Override
    public void beforeCandidateComponent(ClassPathBeanDefinitionScanner scanner) {
        scanner.addIncludeFilter(new AnnotationTypeFilter(DelegateElement.class));

    }

    @Override
    public AnnotatedBeanDefinition afterCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        if(metadata.isAnnotated(DelegateElement.class.getName())) {
            if(metadata.isInterface()||metadata.isAbstract()) {
                throw new IllegalStateException(String.format("%s must not be abstract or a interface", metadata.getClassName()));
            }
            Class interfaceClass = findInterface(metadata);
            if(interfaceClass != null) {
                DelegateManager.addDelegateBeanDefinition(interfaceClass
                        , (String) metadata.getAnnotationAttributes(DelegateElement.class.getName()).get("key")
                        , StringUtil.lowerCaseInitial(metadata.getClassName()));
            }
        }
        return beanDefinition;
    }

    private Class findInterface(AnnotationMetadata metadata) {
        try {
            Class impClass = Class.forName(metadata.getClassName());
            Class[] superInterfaces = impClass.getInterfaces();
            for(Class superInterface : superInterfaces) {
                if(superInterface.isAnnotationPresent(Delegate.class)) {
                    return superInterface;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SciException("ClassNotFoundException for " + e.getMessage());
        }
        return null;
    }
}
