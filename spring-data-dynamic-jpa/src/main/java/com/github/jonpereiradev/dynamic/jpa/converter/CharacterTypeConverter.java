package com.github.jonpereiradev.dynamic.jpa.converter;

public class CharacterTypeConverter implements TypeConverter<Character> {

    @Override
    public Character convertValue(String value) {
        return value.charAt(0);
    }

}