package com.scintillance.common.orm;

import com.scintillance.common.exception.SciException;
import com.scintillance.common.util.SpringUtil;

/**
 * Created by Administrator on 2018/3/3 0003.
 */
public class DataSourceUtil {
    private static ThreadLocal<String> threadLocalContext = new ThreadLocal<>();

    public static void setDataSource(String alias) {
        if(!containDataSource(alias)) {
            throw new SciException("cannot find dataSource for key " + alias);
        }
        threadLocalContext.set(alias);
    }

    public static String getDataSource() {
        return threadLocalContext.get();
    }

    public static void clear() {
        threadLocalContext.remove();
    }

    public static boolean containDataSource(String dataSource) {
        DynamicDataSource dynamicDataSource = SpringUtil.getBean(DynamicDataSource.class);
        return dynamicDataSource.containDataSource(dataSource);
    }
}
