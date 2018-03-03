package com.scintillance.common.resource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/15 0015.
 */
public class ClassPathResourceResolver implements ResourceResolver<Resource[]> {
    private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public String[] getProtocols() {
        return new String[]{"classpath", "classpath*", "file"};
    }

    @Override
    public Resource[] resolve(URI URI) throws IOException {
        return resourcePatternResolver.getResources(URI.getUrl());
    }
}
