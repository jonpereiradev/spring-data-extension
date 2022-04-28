package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

public class QueryInspectorResultImpl implements QueryInspectorResult {

    private boolean distinct;

    private String[] select;
    private FromInspected[] from;
    private String[] join;
    private String[] filter;
    private String[] order;

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public String[] getSelect() {
        return select;
    }

    public void setSelect(String[] select) {
        this.select = select;
    }

    @Override
    public FromInspected[] getFrom() {
        return from;
    }

    public void setFrom(FromInspected[] from) {
        this.from = from;
    }

    @Override
    public String[] getJoin() {
        return join;
    }

    public void setJoin(String[] join) {
        this.join = join;
    }

    @Override
    public String[] getWhere() {
        return filter;
    }

    public void setWhere(String[] filters) {
        this.filter = filters;
    }

    @Override
    public String[] getOrderBy() {
        return order;
    }

    public void setOrderBy(String[] orders) {
        this.order = orders;
    }
}
