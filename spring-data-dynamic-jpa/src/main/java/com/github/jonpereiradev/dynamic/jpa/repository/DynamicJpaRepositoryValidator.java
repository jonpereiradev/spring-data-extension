package com.github.jonpereiradev.dynamic.jpa.repository;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;

public class DynamicJpaRepositoryValidator {

    public boolean isDynamicRepository(Class<?> repositoryInterface) {
        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (anInterface.equals(DynamicJpaRepository.class)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDynamicMethod(Method method) {
        Class<?>[] parameters = method.getParameterTypes();

        if (parameters.length == 1 && parameters[0].equals(DynamicQueryParams.class)) {
            return true;
        }

        if (parameters.length == 2) {
            for (Class<?> parameter : parameters) {
                if (!parameter.isAssignableFrom(DynamicQueryParams.class) || !parameter.isAssignableFrom(Pageable.class)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean isDynamicMethodParametersValid(Method method) {
        Class<?>[] parameters = method.getParameterTypes();

        if (parameters.length == 0 || parameters.length > 2
            || isInvalidParameterClass(parameters, DynamicQueryParams.class)
            || isInvalidParameterClass(parameters, DynamicQueryParams.class, Pageable.class)
        ) {
            return false;
        }

        return true;
    }

    private boolean isInvalidParameterClass(Class<?>[] classes, Class<?>... validClasses) {
        if (validClasses.length != classes.length) {
            return false;
        }

        boolean isAnyClass = true;

        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].equals(validClasses[i])) {
                isAnyClass = false;
                break;
            }
        }

        return !isAnyClass;
    }

}
