package com.github.jonpereiradev.dynamic.jpa.converter;

import java.math.BigDecimal;

public final class BigDecimalTypeConverter implements TypeConverter<BigDecimal> {

    @Override
    public BigDecimal convertValue(String value) {
        return new BigDecimal(value);
    }

}
