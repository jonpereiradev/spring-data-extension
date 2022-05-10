package com.github.jonpereiradev.dynamic.jpa.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class LocalDateTimeTypeConverter implements TypeConverter<LocalDateTime> {

    @Override
    public LocalDateTime convertValue(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

}
