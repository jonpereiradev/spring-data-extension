package com.github.jonpereiradev.dynamic.jpa;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;


final class DynamicQueryReader {

    private DynamicQueryReader() {
    }

    public enum Mapper {
        STRING_TYPE(String.class) {
            @Override
            public String getQuery(String name) {
                return "and lower(o." + name + ") like lower(:" + name + ")";
            }

            @Override
            public Function<Object, ?> getMatcher(Class<?> fieldType) {
                return DynamicQueryMatchers::matchingAny;
            }
        },
        ENUM_TYPE(Enum.class) {
            @Override
            public Function<Object, ?> getMatcher(Class<?> fieldType) {
                return value -> DynamicQueryMatchers.toEnum(fieldType, value);
            }
        },
        LOCAL_DATE_TYPE(LocalDate.class) {
            @Override
            public String getQuery(String name) {
                if (name.toLowerCase().contains("datafim")) {
                    return "and o." + name + " <= :" + name;
                }

                return "and o." + name + " >= :" + name;
            }

            @Override
            public Function<Object, ?> getMatcher(Class<?> fieldType) {
                return DynamicQueryMatchers::toLocalDate;
            }
        },
        LOCAL_DATE_TIME_TYPE(LocalDateTime.class) {
            @Override
            public String getQuery(String name) {
                if (name.toLowerCase().contains("datafim")) {
                    return "and o." + name + " <= :" + name;
                }

                return "and o." + name + " >= :" + name;
            }

            @Override
            public Function<Object, ?> getMatcher(Class<?> fieldType) {
                return DynamicQueryMatchers::toLocalDateTime;
            }
        },
        DEFAULT_TYPE(Object.class);

        private final Class<?> clazz;

        Mapper(Class<?> clazz) {
            this.clazz = clazz;
        }

        public static Mapper valueOf(Class<?> fieldType) {
            for (Mapper value : Mapper.values()) {
                if (value.clazz.isAssignableFrom(fieldType)) {
                    return value;
                }
            }

            return DEFAULT_TYPE;
        }

        public String getQuery(String name) {
            return "and o." + name + " = :" + name;
        }

        public Function<Object, ?> getMatcher(Class<?> fieldType) {
            return value -> DynamicQueryMatchers.from(fieldType, value);
        }
    }

}
