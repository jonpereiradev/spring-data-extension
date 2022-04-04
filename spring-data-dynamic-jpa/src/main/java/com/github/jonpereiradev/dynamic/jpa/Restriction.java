package com.github.jonpereiradev.dynamic.jpa;


import java.util.function.Function;


/**
 * Interface para integrar com o QueryBuilder, onde permite o mapeamento de restrições por uma chave e expressão de
 * SQL.
 */
public class Restriction {

    private final String name;
    private final String clause;
    private final Function<Object, ?> matcher;

    public Restriction(String name, String clause, Function<Object, ?> matcher) {
        this.name = name;
        this.clause = clause;
        this.matcher = matcher;
    }

    public String getName() {
        return name;
    }

    public String getClause() {
        return clause;
    }

    public Function<Object, ?> getMatcher() {
        return matcher;
    }

}
