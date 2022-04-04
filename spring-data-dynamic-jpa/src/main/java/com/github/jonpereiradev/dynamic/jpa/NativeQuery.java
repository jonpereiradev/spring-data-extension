package com.github.jonpereiradev.dynamic.jpa;


/**
 * Define um contrato de objeto que lê uma query nativa de um arquivo SQL.
 *
 * @see NativeQueryImpl
 */
public interface NativeQuery {

    static NativeQuery defaultInstance() {
        return new NativeQueryImpl();
    }

    /**
     * Lê uma query de um arquivo SQL.
     *
     * @param sqlFile nome do arquivo sql que será lido.
     * @return query criada a partir de um arquivo SQL.
     */
    StringBuilder readQuery(String sqlFile);

}
