package com.scintillance.common.util;

import org.springframework.beans.SimpleTypeConverter;

/**
 * Created by Administrator on 2017/12/9 0009.
 */
public class TypeConverter {
    private static SimpleTypeConverter typeConverter = new SimpleTypeConverter();

    public static <S,T> T convert(S source, Class<T> targetClass) {
        if(source == null) return null;
        Assert.notNull(targetClass,"targetClass must not be null");
        return typeConverter.convertIfNecessary(source,targetClass);
    }
}
