package com.people.hub.policyenginecore.service;

import com.people.hub.policyengineapi.service.ContextDefinition;
import com.people.hub.policyengineapi.service.DataSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSourceRegistry {

    private final Map<String, DataSource> dataSourceMap;

    public DataSourceRegistry(List<DataSource> dataSources) {

        dataSourceMap = dataSources.stream()
                .collect(Collectors.toMap(
                        DataSource::getService,
                        Function.identity()
                ));
    }

    public DataSource getDataSource(String service) {
        return dataSourceMap.get(service);
    }
}
