package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.core.MethodParameter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

import static java.util.Arrays.asList;


public class DynamicQueryArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        Map<String, String[]> parameters = webRequest.getParameterMap();
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        DynamicQueryParams params = DynamicQueryParams.newInstance();

        parameters.forEach((k, v) -> multiValueMap.put(k, asList(v)));
        multiValueMap.forEach(params::addParameter);

        return params;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(DynamicQueryParams.class);
    }

}
