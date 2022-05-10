package com.github.jonpereiradev.dynamic.jpa.converter;

final class StringTypeConverter implements TypeConverter<String> {

    @Override
    public String convertValue(String value) {
        return value;
    }

}
