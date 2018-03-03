package com.scintillance.common.annotation;

import com.scintillance.common.orm.MultiDataSourceRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MultiDataSourceRegistrar.class)
public @interface EnabledMultiDataSource {
}
