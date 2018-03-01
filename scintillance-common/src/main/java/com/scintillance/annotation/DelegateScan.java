package com.scintillance.annotation;

import com.scintillance.registry.DelegateScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DelegateScanRegistrar.class)
public @interface DelegateScan {
    String[] value() default {};
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
