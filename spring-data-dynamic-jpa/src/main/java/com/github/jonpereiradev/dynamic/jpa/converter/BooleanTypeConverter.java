package com.github.jonpereiradev.dynamic.jpa.converter;

public final class BooleanTypeConverter implements TypeConverter<Boolean> {

    @Override
    public Boolean convertValue(String value) {
        return "true".equals(value.toLowerCase());
    }

}
