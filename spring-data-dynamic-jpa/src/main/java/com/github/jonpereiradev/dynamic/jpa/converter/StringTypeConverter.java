package com.github.jonpereiradev.dynamic.jpa.converter;

public class StringTypeConverter implements TypeConverter<String> {

    @Override
    public String convertValue(String value) {
        return value;
    }

}
