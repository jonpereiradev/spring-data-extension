package com.github.jonpereiradev.dynamic.jpa.query;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;


final class DynamicQueryImpl extends AbstractDynamicQuery {

    @Override
    public void addParameter(String name, Object value) {
        if (value instanceof List) {
            parameters.addAll(name, (List<?>) value);
        } else if (value instanceof Set) {
            parameters.addAll(name, new ArrayList<>((Set<?>) value));
        } else {
            parameters.add(name, value);
        }
    }

    @Override
    public void setParameter(String name, Object value) {
        parameters.remove(name);
        addParameter(name, value);
    }
}
