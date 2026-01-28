package com.partyguham.global.config;

import com.partyguham.global.annotation.ApiV2Controller;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v2",
                c -> c.isAnnotationPresent(ApiV2Controller.class)
        );
    }
}