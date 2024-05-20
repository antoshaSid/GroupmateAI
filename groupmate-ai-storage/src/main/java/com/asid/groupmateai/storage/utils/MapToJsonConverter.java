package com.asid.groupmateai.storage.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }

        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(final String data) {
        if (data == null || data.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return MAPPER.readValue(data, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}

