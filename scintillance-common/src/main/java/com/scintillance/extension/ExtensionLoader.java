package com.scintillance.extension;

import com.scintillance.annotation.SPI;
import com.scintillance.exception.SciException;
import com.scintillance.util.ClassUtil;
import com.scintillance.util.PriorityComparator;
import com.scintillance.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2018/1/10 0010.
 * 扩展点构造
 */
@Slf4j
public class ExtensionLoader<T> {

    private static ConcurrentMap<Class, ExtensionLoader> cachedExtensionLoaders = new ConcurrentHashMap<>();

    private String BAMBOO_DIRECTORY = "META-INF/sci/";

    private Class<?> type;

    private Map<String,Class> cachedClasses;

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<?> type) {
        Assert.notNull(type, "type must be not null");
        if(!type.isInterface()) {
            throw new IllegalArgumentException(String.format("type[%s] must be a interface", type.getName()));
        }

        if(!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException(String.format("type[%s] without the annotation @SPI[%s]", type.getName(), SPI.class.getName()));
        }

        ExtensionLoader extensionLoader = cachedExtensionLoaders.get(type);
        if(extensionLoader == null) {
            cachedExtensionLoaders.putIfAbsent(type, new ExtensionLoader(type));
            extensionLoader = cachedExtensionLoaders.get(type);
        }

        return extensionLoader;
    }

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public Collection<T> getExtensionCollection() {
        return getExtensions().values();
    }

    public T getExtension(String key) {
        return getExtensions(key).get(key);
    }

    public Map<String, T> getExtensions(String... keys) {
        loadClasses();

        Map<String, T> extensionMap = new HashMap<String, T>();
        if(keys.length == 0) {
            Set<String> keySet = cachedClasses.keySet();
            keys = keySet.toArray(new String[keySet.size()]);
        }

        for(String key : keys) {
            Class<T> clazz = cachedClasses.get(key);
            if(clazz == null) {
                log.warn(String.format("the key[%s] cannot found in the map of cachedClasses", key));
            }else {
                Throwable throwable = null;
                try {
                    T instance = clazz.newInstance();
                    extensionMap.put(key, instance);
                } catch (IllegalAccessException e) {
                    throwable = e;
                } catch (InstantiationException e) {
                    throwable = e;
                }

                if(throwable != null) {
                    throw new SciException("occurred error in the function: getExtension", throwable);
                }
            }
        }
        return extensionMap;
    }

    public void loadClasses() {
        if(cachedClasses!=null) {
            return;
        }else {
            cachedClasses = new HashMap<>();
        }

        String filePath = BAMBOO_DIRECTORY + type.getName();
        try {
            Map<String, Object> map = new HashMap<>();
            Resource[] resources = ClassUtil.scanFile(filePath);
            for(Resource resource : resources) {
                PropertiesUtil.getPropertiesFromInputStream(map,resource.getInputStream());
            }

            for(Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();

                try {
                    Class clazz = Class.forName(value);
                    if(!type.isAssignableFrom(clazz)) {
                        throw new SciException(String.format("the class[%s] must implement the interface[%s]", clazz.getName(), type.getName()));
                    }
                    Class existClass = cachedClasses.get(key);
                    if(existClass == null || PriorityComparator.priorThan(clazz, existClass)) {
                        cachedClasses.put(key, clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new SciException(String.format("cannot found class[%s]", value),e);
                }
            }
        } catch (IOException e) {
            throw new SciException(String.format("failed to scan file[%s]", filePath), e);
        }
    }

}
