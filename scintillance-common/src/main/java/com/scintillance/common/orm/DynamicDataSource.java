package com.scintillance.common.orm;

import com.scintillance.common.compiler.ClassHelper;
import com.scintillance.common.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.bind.PropertySourceUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
@EnableConfigurationProperties(DataSourceProperties.class)
public class DynamicDataSource extends AbstractRoutingDataSource implements EnvironmentAware{

    private static final String PREFIX = "spring.datasource.custom.";
    private static final String USER_SUFFIX = "username";
    private static final String URL_SUFFIX = "url";
    private static final String PASSWORD_SUFFIX = "password";
    private static final String DRIVER_SUFFIX = "driver-class-name";

    @Autowired
    private DataSourceProperties defaultDataSourceProperties;

    private Map<String, String> propertyMap;

    private Map<Object, Object> targetDataSources = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        DataSource defaultDataSource = defaultDataSourceProperties.initializeDataSourceBuilder().build();
        setDefaultTargetDataSource(defaultDataSource);
        targetDataSources.put("default", defaultDataSource);

        Map<String,Object> customProperties = PropertySourceUtils.getSubProperties(((ConfigurableEnvironment)environment).getPropertySources(), PREFIX);
        List<String> alias = getAlias(customProperties);
        for(String alia : alias) {
            DataSource dataSource = createDataSource(alia, customProperties);
            targetDataSources.put(alia, dataSource);
        }
        
        setTargetDataSources(targetDataSources);
    }


    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceUtil.getDataSource();
    }

    public boolean containDataSource(String dataSource) {
        return targetDataSources.containsKey(dataSource);
    }

    private List<String> getAlias(Map<String,Object> customProperties) {
        List<String> alias = new ArrayList<>();
        for(Map.Entry<String, Object> entry : customProperties.entrySet()) {
            String key = entry.getKey();
            String alia = null;
            if(key.contains(".")) {
                alia = key.substring(0, key.indexOf('.'));
            }else {
                alia = key;
            }
            alias.add(alia);
        }
        return alias;
    }

    private DataSource createDataSource(String alia, Map<String, Object> propertyMap) {
        String prefix = PREFIX + alia + ".";
        String url = (String) propertyMap.get(prefix + URL_SUFFIX);
        String username = (String) propertyMap.get(prefix + USER_SUFFIX);
        String password = (String) propertyMap.get(prefix + PASSWORD_SUFFIX);
        String driverClass = (String) propertyMap.get(prefix + DRIVER_SUFFIX);

        Assert.notNull(url, "cannot find url for alias :" + alia);
        Assert.notNull(username, "cannot find username for alias :" + alia);
        Assert.notNull(password, "cannot find password for alias :" + alia);
        Assert.notNull(driverClass, "cannot find driver-class-name for alias :" + alia);


        return DataSourceBuilder
                .create(ClassHelper.getCallerClassLoader(getClass()))
                .type(defaultDataSourceProperties.getType())
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClass)
                .build();
    }
}
