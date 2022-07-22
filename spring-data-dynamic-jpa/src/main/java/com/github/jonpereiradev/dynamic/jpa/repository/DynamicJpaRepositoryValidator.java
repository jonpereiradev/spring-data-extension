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

        return parameters.length != 0 && parameters.length <= 2
            && isValidParameterClass(parameters, DynamicQueryParams.class)
            && isValidParameterClass(parameters, DynamicQueryParams.class, Pageable.class);
    }

    private boolean isValidParameterClass(Class<?>[] classes, Class<?>... validClasses) {
        if (validClasses.length != classes.length) {
            return true;
        }

        boolean isAnyClass = true;

        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].equals(validClasses[i])) {
                isAnyClass = false;
                break;
            }
        }

        return isAnyClass;
    }

}
