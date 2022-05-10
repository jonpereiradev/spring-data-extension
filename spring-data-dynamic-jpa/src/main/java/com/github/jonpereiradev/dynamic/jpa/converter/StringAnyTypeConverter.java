package com.github.jonpereiradev.dynamic.jpa.converter;

public class StringAnyTypeConverter implements TypeConverter<String> {

    @Override
    public String convertValue(String value) {
        return "%" + value + "%";
    }
}