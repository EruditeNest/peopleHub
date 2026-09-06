package com.people.hub.policyenginecore.service;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.service.DataSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSourceRegistry {

    private final Map<String, DataSource> dataSourceMap;

    private final Map<AttributeIdentifier, List<DataSource>> providers;

    public DataSourceRegistry(List<DataSource> dataSources) {

        dataSourceMap = dataSources.stream()
                .collect(Collectors.toMap(
                        DataSource::getService,
                        Function.identity()
                ));

        providers = dataSources.stream()
                .flatMap(dataSource ->
                        dataSource.listSupportedAttributes()
                                .keySet()
                                .stream()
                                .map(attribute ->
                                        Map.entry(attribute, dataSource)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toList()
                        )
                ));
    }

    public DataSource getDataSource(String service) {
        return dataSourceMap.get(service);
    }

    public List<DataSource> getDataSources(AttributeIdentifier attribute) {
        return providers.getOrDefault(attribute, List.of());
    }
}
