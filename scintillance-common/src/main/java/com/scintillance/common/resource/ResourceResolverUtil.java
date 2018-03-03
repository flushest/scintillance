package com.scintillance.common.resource;

import com.scintillance.common.exception.SciException;
import com.scintillance.common.extension.ExtensionLoader;
import com.scintillance.common.util.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/9 0009.
 */
@Slf4j
public class ResourceResolverUtil {
    private static final Map<String,ResourceResolver> protocolAndResolverMap = new HashMap<>();

    static {
        ExtensionLoader<ResourceResolver> extensionLoader = ExtensionLoader.getExtensionLoader(ResourceResolver.class);
        Collection<ResourceResolver> resourceResolvers = extensionLoader.getExtensionCollection();
        registerResolvers(resourceResolvers);
    }

    public static void registerResolver(ResourceResolver resourceResolver) {
        String[] protocols = resourceResolver.getProtocols();

        for( String protocol : protocols) {
            if(protocolAndResolverMap.containsKey(protocol)) {
                throw new SciException(String.format("协议:%s 解析器已经被注册，请检查类%s",protocol,resourceResolver.getClass().getName()));
            }
            protocolAndResolverMap.put(protocol,resourceResolver);
            log.info(String.format("协议:%s 解析器注册成功，解析器->%s",protocol,resourceResolver.getClass().getName()));
        }
    }

    public static void registerResolvers(Collection<ResourceResolver> resourceResolvers) {
        Assert.notNull(resourceResolvers,"非法参数:resourceResolver，不能传null");
        resourceResolvers.forEach((resourceResolver -> registerResolver(resourceResolver)));
    }

    public static <T> T getResource(URI URI) throws IOException {
        String protocol = URI.getProtocol();
        if(!protocolAndResolverMap.containsKey(protocol)) {
            throw new SciException(String.format("协议:%s 没有注册，请检查url:%s",protocol, URI.getUrl()));
        }
        ResourceResolver resolver = protocolAndResolverMap.get(URI.getProtocol());
        return (T) resolver.resolve(URI);
    }

    public static <T> T getResource(String url) throws IOException {
        return getResource(new URI(url));
    }
}
