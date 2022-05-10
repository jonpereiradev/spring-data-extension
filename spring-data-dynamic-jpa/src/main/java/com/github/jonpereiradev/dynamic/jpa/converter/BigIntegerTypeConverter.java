package com.github.jonpereiradev.dynamic.jpa.converter;

import java.math.BigInteger;

final class BigIntegerTypeConverter implements TypeConverter<BigInteger> {

    @Override
    public BigInteger convertValue(String value) {
        return new BigInteger(value);
    }

}