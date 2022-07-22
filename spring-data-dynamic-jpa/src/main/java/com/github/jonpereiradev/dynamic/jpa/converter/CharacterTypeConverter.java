package com.github.jonpereiradev.dynamic.jpa.converter;

public final class CharacterTypeConverter implements TypeConverter<Character> {

    @Override
    public Character convertValue(String value) {
        return value.charAt(0);
    }

}
