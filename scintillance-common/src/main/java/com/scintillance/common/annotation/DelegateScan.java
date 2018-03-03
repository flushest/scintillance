package com.scintillance.common.annotation;

import com.scintillance.common.registry.DelegateScanRegistrar;
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
