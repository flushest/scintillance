package com.scintillance.registry;

import com.scintillance.annotation.Delegate;
import com.scintillance.annotation.RouteProperty;
import com.scintillance.exception.SciException;
import com.scintillance.exception.SciNotFoundRoutePropertyException;
import com.scintillance.extension.DelegateManager;
import com.scintillance.util.ClassUtil;
import com.scintillance.util.SpringUtil;
import com.scintillance.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        return beanDefinition;
    }

    private AnnotatedBeanDefinition createProxyBeanDefinition(String beanClassName) {
        try {
            Class interfaceClass = Class.forName(beanClassName);
            Class proxyClass = createProxyClass(interfaceClass);
            DelegateManager.addDelegateProxy(interfaceClass, proxyClass);
            return new AnnotatedGenericBeanDefinition(proxyClass);
        } catch (ClassNotFoundException e) {
            throw new SciException("ClassNotFoundException for " + e.getMessage());
        }
    }


    private Class createProxyClass(Class interfaceClass) throws ClassNotFoundException {
        String code = generateProxyCode(interfaceClass);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaFileManager manager= compiler.getStandardFileManager(collector, null, null);
        List<String> options = new ArrayList<>();
        options.add("-target");
        options.add("1.8");
        String className = "com.scintillance.proxy."+interfaceClass.getSimpleName() + "Delegate";
        JavaFileObject javaFileObject = new StringJavaFileObject(className, code);
        boolean result = compiler.getTask(null, manager, collector, options, null, Arrays.asList(javaFileObject)).call();
        if(result) {
            return Class.forName(className);
        }else {
            throw new SciException("failed to create delegate proxy for interface" + interfaceClass.getName());
        }
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
