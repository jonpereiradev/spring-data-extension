package com.github.jonpereiradev.dynamic.jpa.query;


import com.github.jonpereiradev.dynamic.jpa.query.DynamicJoin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicJoins {

    DynamicJoin[] value();

}
