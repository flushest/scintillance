package com.scintillance.common.orm;

/**
 * Created by Administrator on 2018/3/3 0003.
 */
public class DataSourceUtil {
    private static ThreadLocal<String> threadLocalContext = new ThreadLocal<>();

    public static void setDataSource(String alias) {
        threadLocalContext.set(alias);
    }

    public static String getDataSource() {
        return threadLocalContext.get();
    }

    public static void clear() {
        threadLocalContext.remove();
    }
}
