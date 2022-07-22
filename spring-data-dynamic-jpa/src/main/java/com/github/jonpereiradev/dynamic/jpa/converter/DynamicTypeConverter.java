package com.github.jonpereiradev.dynamic.jpa.converter;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class DynamicTypeConverter {

    private static final Map<Class<?>, TypeConverter<?>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(char.class, new CharacterTypeConverter());
        CONVERTERS.put(Character.class, new CharacterTypeConverter());
        CONVERTERS.put(String.class, new StringTypeConverter());
        CONVERTERS.put(short.class, new ShortTypeConverter());
        CONVERTERS.put(Short.class, new ShortTypeConverter());
        CONVERTERS.put(int.class, new IntegerTypeConverter());
        CONVERTERS.put(Integer.class, new IntegerTypeConverter());
        CONVERTERS.put(long.class, new LongTypeConverter());
        CONVERTERS.put(Long.class, new LongTypeConverter());
        CONVERTERS.put(boolean.class, new BooleanTypeConverter());
        CONVERTERS.put(Boolean.class, new BooleanTypeConverter());
        CONVERTERS.put(LocalDate.class, new LocalDateTypeConverter());
        CONVERTERS.put(LocalDateTime.class, new LocalDateTimeTypeConverter());
        CONVERTERS.put(UUID.class, new UUIDTypeConverter());
        CONVERTERS.put(BigInteger.class, new BigIntegerTypeConverter());
        CONVERTERS.put(BigDecimal.class, new BigDecimalTypeConverter());
        CONVERTERS.put(Void.class, new NoneTypeConverter());
        CONVERTERS.put(AutodetectTypeConverter.class, new NoneTypeConverter());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> TypeConverter<T> get(Class<T> clazz) {
        if (CONVERTERS.containsKey(clazz)) {
            return (TypeConverter<T>) CONVERTERS.get(clazz);
        }

        if (Enum.class.isAssignableFrom(clazz) && !CONVERTERS.containsKey(clazz)) {
            CONVERTERS.put(clazz, new EnumTypeConverter(clazz));
        }

        if (!TypeConverter.class.isAssignableFrom(clazz)) {
            CONVERTERS.put(clazz, new NoneTypeConverter());
            return (TypeConverter<T>) CONVERTERS.get(clazz);
        }

        if (!CONVERTERS.containsKey(clazz)) {
            try {
                Constructor<T> constructor = clazz.getConstructor();
                TypeConverter<T> typeConverter = (TypeConverter<T>) constructor.newInstance();
                CONVERTERS.put(clazz, typeConverter);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new TypeConverterException(clazz, e);
            }
        }

        return (TypeConverter<T>) CONVERTERS.get(clazz);
    }

    public static <T> T convertValue(Class<T> clazz, Object value) {
        if (value == null || value.toString() == null || value.toString().trim().isEmpty()) {
            return null;
        }

        return get(clazz).convertValue(value.toString());
    }

}
