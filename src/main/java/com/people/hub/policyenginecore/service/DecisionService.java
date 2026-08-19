package com.people.hub.policyenginecore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.people.hub.common.exception.DecisionValidationException;
import com.people.hub.policyengineapi.dto.DecisionDefinition;
import com.people.hub.policyengineapi.dto.DecisionFieldDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionService {
    private final DecisionDefinitionRegistry definitionRegistry;

    public DecisionDefinition getDecisionDefinition(String service, String contextCode) {
        return definitionRegistry.getDefinition(service, contextCode);
    }

    public void validateDecision(String service, String contextCode, JsonNode decision) {
        DecisionDefinition definition = getDecisionDefinition(service, contextCode);

        List<String> errors = new ArrayList<>();

        if (decision == null || decision.isNull()) {
            throw new IllegalArgumentException("Decision cannot be null");
        }
        if (!decision.isObject()) {
            throw new IllegalArgumentException("Decision must be a JSON object");
        }
        for (DecisionFieldDefinition field : definition.getFields()) {
            JsonNode value = decision.get(field.getName());
            try {
                validateRequiredField(field, value);
                if (value != null && !value.isNull()) {
                    validateFieldDataType(field, value);
                    validateFieldAllowedValues(field, value);
                }
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            throw new DecisionValidationException(contextCode, errors);
        }
    }

    private void validateRequiredField(DecisionFieldDefinition field, JsonNode value) {
        if (!field.isRequired()) {
            return;
        }
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException(
                    "Required decision field is missing: "
                            + field.getName()
            );
        }

        if (value.isTextual() && value.asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Required decision field cannot be blank: "
                            + field.getName()
            );
        }
    }

    private void validateFieldDataType(
            DecisionFieldDefinition field,
            JsonNode value
    ) {
        boolean valid = switch (field.getDataType()) {
            case STRING -> value.isTextual();

            case INTEGER -> value.isIntegralNumber() && value.canConvertToInt();

            case LONG -> value.isIntegralNumber() && value.canConvertToLong();

            case DECIMAL -> value.isNumber();

            case BOOLEAN -> value.isBoolean();

            case DATETIME -> isValidDateTime(value);

            case ENUM -> value.isTextual();
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid type for decision field '"
                            + field.getName()
                            + "'. Expected "
                            + field.getDataType()
            );
        }
    }

    private void validateFieldAllowedValues(
            DecisionFieldDefinition field,
            JsonNode value
    ) {
        List<String> allowedValues = field.getAllowedValues();
        if (allowedValues == null || allowedValues.isEmpty()) {
            return;
        }
        if (!value.isTextual()) {
            return; // type validation will already report this
        }
        String actualValue = value.asText();
        if (!allowedValues.contains(actualValue)) {
            throw new IllegalArgumentException(
                    "Invalid value for decision field '"
                            + field.getName()
                            + "'. "
                            + "Allowed values: "
                            + allowedValues
            );
        }
    }

    private boolean isValidDateTime(JsonNode value) {
        if (!value.isTextual()) {
            return false;
        }
        try {
            LocalDateTime.parse(value.asText());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
