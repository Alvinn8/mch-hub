package com.mch.hub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RepositoryPasswordInterceptor repositoryPasswordInterceptor;

    public WebConfig(RepositoryPasswordInterceptor repositoryPasswordInterceptor) {
        this.repositoryPasswordInterceptor = repositoryPasswordInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repositoryPasswordInterceptor)
                .addPathPatterns("/api/repos/{ownerSlug}/{repoSlug}/**");
    }
}

