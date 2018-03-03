package com.scintillance.common.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/2/27 0027.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {
    String name() default "";
}
