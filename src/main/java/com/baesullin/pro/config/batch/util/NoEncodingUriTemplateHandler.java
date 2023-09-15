package com.baesullin.pro.config.batch.util;

import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.util.Map;

public class NoEncodingUriTemplateHandler implements UriTemplateHandler {
    private final DefaultUriBuilderFactory uriBuilderFactory;

    public NoEncodingUriTemplateHandler() {
        this.uriBuilderFactory = new DefaultUriBuilderFactory();
        this.uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
    }

    @Override
    public URI expand(String uriTemplate, Object... uriVariables) {
        return uriBuilderFactory.expand(uriTemplate, uriVariables);
    }

    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
        return uriBuilderFactory.expand(uriTemplate, uriVariables);
    }
}
