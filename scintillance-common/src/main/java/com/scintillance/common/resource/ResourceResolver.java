package com.scintillance.common.resource;

import com.scintillance.common.annotation.SPI;

import java.io.IOException;

/**
 * Created by Administrator on 2017/12/9 0009.
 * 资源解析器
 */
@SPI
public interface ResourceResolver<T> {
    String[] getProtocols();
    T resolve(URI URI) throws IOException;
}
