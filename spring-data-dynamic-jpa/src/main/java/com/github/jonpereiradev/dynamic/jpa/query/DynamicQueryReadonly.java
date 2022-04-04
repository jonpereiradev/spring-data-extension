package com.github.jonpereiradev.dynamic.jpa.query;


final class DynamicQueryReadonly extends AbstractDynamicQuery {

    DynamicQueryReadonly(DynamicQuery mapQuery) {
        mapQuery.getParameters().forEach(parameters::addAll);
    }

    @Override
    public void addParameter(String name, Object value) {
        throw new IllegalStateException("The state can't be modified.");
    }

    @Override
    public void setParameter(String name, Object value) {
        throw new IllegalStateException("The state can't be modified.");
    }
}
