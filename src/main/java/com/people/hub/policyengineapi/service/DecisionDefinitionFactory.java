package com.people.hub.policyengineapi.service;

import com.people.hub.policyengineapi.annotation.DecisionField;
import com.people.hub.policyengineapi.dto.DecisionDefinition;
import com.people.hub.policyengineapi.dto.DecisionFieldDefinition;
import com.people.hub.policyengineapi.enums.DataType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Component
public class DecisionDefinitionFactory {

    public DecisionDefinition create(Decision decision) {

        List<DecisionFieldDefinition> fields =
                Arrays.stream(decision.getClass().getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(DecisionField.class))
                        .map(this::createFieldDefinition)
                        .toList();

        return new DecisionDefinition(
                decision.getCode(),
                decision.getDisplayName(),
                fields
        );
    }

    private DecisionFieldDefinition createFieldDefinition(Field field) {
        DecisionField annotation =
                field.getAnnotation(DecisionField.class);

        return new DecisionFieldDefinition(
                field.getName(),
                DataType.fromJavaType(field.getType()),
                annotation.required(),
                resolveDisplayName(field, annotation),
                annotation.description(),
                getAllowedValues(field.getType())
        );
    }

    private String resolveDisplayName(
            Field field,
            DecisionField annotation
    ) {
        if (!annotation.displayName().isBlank()) {
            return annotation.displayName();
        }

        return field.getName();
    }

    private List<String> getAllowedValues(Class<?> fieldType) {
        if (!fieldType.isEnum()) {
            return null;
        }
        return Arrays.stream(fieldType.getEnumConstants())
                .map(value -> ((Enum<?>) value).name())
                .toList();
    }
}
