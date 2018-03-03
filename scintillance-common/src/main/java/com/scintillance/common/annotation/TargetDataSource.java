package com.scintillance.common.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/3/3 0003.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
