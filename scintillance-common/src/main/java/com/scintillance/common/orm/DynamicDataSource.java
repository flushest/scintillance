package com.scintillance.common.orm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/2 0002.
 */
@EnableConfigurationProperties(DataSourceProperties.class)
public class DynamicDataSource extends AbstractRoutingDataSource implements EnvironmentAware{

    private static final String PREFIX = "spring.datasource";
    private static final String USER_SUFFIX = "username";
    private static final String URL_SUFFIX = "url";
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

        setTargetDataSources(targetDataSources);
    }


    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceUtil.getDataSource();
    }
}
