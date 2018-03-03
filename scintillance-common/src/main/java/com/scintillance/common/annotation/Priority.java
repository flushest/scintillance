package com.scintillance.common.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/1/14 0014.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Priority {
    int value() default Integer.MIN_VALUE;
}
