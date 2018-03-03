package com.scintillance.common.registry;

import com.scintillance.common.annotation.Delegate;
import com.scintillance.common.annotation.RouteProperty;
import com.scintillance.common.compiler.ClassHelper;
import com.scintillance.common.exception.SciNotFoundRoutePropertyException;
import com.scintillance.common.extension.DelegateManager;
import com.scintillance.common.extension.ExtensionLoader;
import com.scintillance.common.util.ClassUtil;
import com.scintillance.common.exception.SciException;
import com.scintillance.common.util.SpringUtil;
import com.scintillance.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import com.scintillance.common.compiler.Compiler;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;


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
            createProxyBeanClass(metadata.getClassName());
        }
        return beanDefinition;
    }

    private void createProxyBeanClass(String beanClassName) {
        try {
            Class interfaceClass = Class.forName(beanClassName);
            Class proxyClass = createProxyClass(interfaceClass);
            DelegateManager.addDelegateProxy(interfaceClass, proxyClass);
        } catch (ClassNotFoundException e) {
            throw new SciException("ClassNotFoundException for " + e.getMessage());
        }
    }


    private Class createProxyClass(Class interfaceClass) throws ClassNotFoundException {
        String code = generateProxyCode(interfaceClass);
        Compiler compiler = (Compiler) ExtensionLoader.getExtensionLoader(Compiler.class).getDefaultExtension();
        return compiler.compile(code, ClassHelper.getCallerClassLoader(getClass()));
    }

    public String generateProxyCode(Class interfaceClass) {
        StringBuffer codeBuffer = new StringBuffer("package com.scintillance.proxy;\n");
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
            codeBuffer.append(generateExecutePart(method));
            codeBuffer.append("}\n");


        }

        codeBuffer.append("}\n");

        return codeBuffer.toString();
    }

    private String generateExecutePart(Method method) {
        StringBuffer executePart = new StringBuffer();

        String route = "";

        Class<?>[] pts = method.getParameterTypes();
        for(int i=0; i<pts.length; i++) {
            Class pt = pts[i];
            Field field = ClassUtil.getDeclaredFieldByAnnotation(pt, RouteProperty.class);
            if(field != null) {
                route += "p"+ i + ".get" + StringUtil.upCaseInitial(field.getName()) + "();";
            }
        }

        if(StringUtil.isEmpty(route)) {
            throw new SciNotFoundRoutePropertyException("cannot find @RouteProperty parameterType for method " + method.getName());
        }

        executePart.append("String routeKey = ").append(route).append("\n");
        executePart.append("String beanName = ").append(DelegateManager.class.getName())
                .append(".getBeanName(").append(method.getDeclaringClass().getName()).append(".class,")
                .append("routeKey);\n");
        executePart.append("if(").append(StringUtil.class.getName()).append(".isEmpty(beanName)){\n");
        executePart.append("throw new IllegalArgumentException(\"cannot find beanName for\" + routeKey);\n");
        executePart.append("}\n");
        executePart.append(method.getDeclaringClass().getName()).append(" obj = ").append(SpringUtil.class.getName()).append(".getBean(beanName);\n");

        if(!"void".equals(method.getReturnType().getName())) {
            executePart.append("return ");
        }

        executePart.append("obj.").append(method.getName()).append("(");
        for(int i=0; i<pts.length; i++) {
            executePart.append("p" + i);
            if(i<pts.length-1) {
                executePart.append(",");
            }
        }
        executePart.append(");\n");
        return executePart.toString();
    }

    public static class StringJavaFileObject extends SimpleJavaFileObject {
        private String source;
        private ByteArrayOutputStream outPutStream;
        // 该构造器用来输入源代码
        public StringJavaFileObject(String name, String source) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors){
            if(source == null){
                throw new IllegalArgumentException("source == null");
            }
            return source;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            outPutStream = new ByteArrayOutputStream();
            return outPutStream;
        }

        // 获取编译成功的字节码byte[]
        public byte[] getCompiledBytes(){
            return outPutStream.toByteArray();
        }
    }
}
