package com.genev.a100nts.server.utils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHARACTER = ";";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute != null
                ? String.join(SPLIT_CHARACTER, attribute)
                : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData != null
                ? Arrays.stream(dbData.split(SPLIT_CHARACTER))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

}
