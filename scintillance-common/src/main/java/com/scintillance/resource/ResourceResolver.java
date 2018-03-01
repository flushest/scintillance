package com.scintillance.resource;

import java.io.IOException;

/**
 * Created by Administrator on 2017/12/9 0009.
 * 资源解析器
 */
public interface ResourceResolver<T> {
    String[] getProtocols();
    T resolve(URI URI) throws IOException;
}
