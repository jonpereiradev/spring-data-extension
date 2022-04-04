package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Responsável por carregar queries nativas de um arquivo sql armazenado em src/main/resources/queries.
 */
@Lazy
@Primary
@Component
final class NativeQueryImpl implements NativeQuery {

    private static final String QUERY_CLAUSE_SEPARATOR = " ";
    /**
     * Cache de queries já lidas de um arquivo sql.
     */
    private final Map<String, String> nativeQueries = new ConcurrentHashMap<>();

    @Value("${mctic.configuration.native-query-root:native-query}")
    private String nativeQueryRoot = "native-query";

    /**
     * Carrega a query de um arquivo sql disponível em src/main/resources/{nativeQueryRoot} e armazena em cache.
     *
     * @param sqlFileName nome do arquivo sql que será lido.
     * @return query recuperada do arquivo sql.
     */
    @Override
    public StringBuilder readQuery(String sqlFileName) {
        if (!nativeQueries.containsKey(sqlFileName)) {
            Path pathToResource = Paths.get(nativeQueryRoot, sqlFileName);
            Resource resource = new ClassPathResource(pathToResource.toString());
            StringBuilder queryBuilder = readFileContent(resource);

            nativeQueries.put(pathToResource.toString(), queryBuilder.toString());

            return queryBuilder;
        }

//        return new DynamicQueryBuilder(nativeQueries.get(sqlFileName));
        return null;
    }

    private StringBuilder readFileContent(Resource resource) {
        StringBuilder queryBuilder = new StringBuilder(null);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
//            reader.lines().forEach(line -> queryBuilder.append(line.trim()).append(QUERY_CLAUSE_SEPARATOR));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return queryBuilder;
    }

}
