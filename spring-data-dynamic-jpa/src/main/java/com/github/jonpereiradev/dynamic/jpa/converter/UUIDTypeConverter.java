package com.github.jonpereiradev.dynamic.jpa.converter;

import java.util.UUID;

public class UUIDTypeConverter implements TypeConverter<UUID> {

    @Override
    public UUID convertValue(String value) {
        return UUID.fromString(value);
    }

}
