package com.github.jonpereiradev.dynamic.jpa.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeConverter implements TypeConverter<LocalDate> {

    @Override
    public LocalDate convertValue(String value) {
        if (value.contains("T")) {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        }

        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
    }

}
