package com.github.jonpereiradev.dynamic.jpa.converter;

import java.math.BigDecimal;

final class BigDecimalTypeConverter implements TypeConverter<BigDecimal> {

    @Override
    public BigDecimal convertValue(String value) {
        return new BigDecimal(value);
    }

}
