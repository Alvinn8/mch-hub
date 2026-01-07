package com.mch.hub.error;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, @Nullable Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, @Nullable Object> attributes = super.getErrorAttributes(webRequest, options);
        Throwable throwable = getError(webRequest);
        if (throwable instanceof RepositoryPasswordException) {
            attributes.put("passwordRequired", true);
        }
        return attributes;
    }
}
