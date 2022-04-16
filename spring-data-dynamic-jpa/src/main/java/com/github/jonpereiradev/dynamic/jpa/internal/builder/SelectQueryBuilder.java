package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

public interface SelectQueryBuilder {

    FromQueryBuilder from();

}
