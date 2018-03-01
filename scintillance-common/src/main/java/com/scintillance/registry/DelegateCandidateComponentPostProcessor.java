package com.scintillance.registry;

import com.scintillance.annotation.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Slf4j
public class DelegateCandidateComponentPostProcessor implements CandidateComponentPostProcessor {
    @Override
    public void beforeCandidateComponent(ClassPathBeanDefinitionScanner scanner) {
        scanner.addIncludeFilter(new AnnotationTypeFilter(Delegate.class));
    }

    @Override
    public AnnotatedBeanDefinition afterCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        if(metadata.hasAnnotation(Delegate.class.getName())) {
            if(!metadata.isInterface()) {
                throw new IllegalStateException(String.format("%s must be a interface", metadata.getClassName()));
            }
            return createProxyBeanDefinition(metadata.getClassName());
        }
    }

    private AnnotatedBeanDefinition createProxyBeanDefinition(String beanClassName) {
        try {
            Class interfaceClass = Class.forName(beanClassName);

        } catch (ClassNotFoundException e) {

        }
    }


    private Class createProxyClass(Class interfaceClass) {
        String code =
    }

    private String generateProxyCode(Class interfaceClass) {
        StringBuffer codeBuffer = new StringBuffer("package com.scintillance.proxy").append("\n");
        codeBuffer.append("import com.scintillance.extension.DelegateManager;\n");
        codeBuffer.append("public class ").append(interfaceClass.getSimpleName()).append("Delegate")
                .append(" implements ").append(interfaceClass.getName()).append(" {\n");

        Method[] methods = interfaceClass.getMethods();
        for(Method method : methods) {
            Class<?> rt = method.getReturnType();
            Class<?>[] pts = method.getParameterTypes();
            Class<?>[] ets = method.getExceptionTypes();

            codeBuffer.append("public ").append(rt.getName())
                    .append(" ").append(method.getName())
                    .append("(");
            for(int i=0; i<pts.length; i++) {
                Class pt = pts[i];
                codeBuffer.append(pt.getName()).append(" p").append(i);
                if(i<pts.length-1) {
                    codeBuffer.append(", ");
                }
            }
            codeBuffer.append(") ");

            if(ets.length > 0) {
                codeBuffer.append("throw ");
            }
            for(int i=0; i<ets.length; i++) {
                Class et = ets[i];
                codeBuffer.append(et.getName());
                if(i < ets.length-1) {
                    codeBuffer.append(",");
                }
            }

            codeBuffer.append(" {\n");

            codeBuffer.append("}\n");


        }

        codeBuffer.append("}\n");
    }

    private Field getAnnotationField() {

    }
}
