package com.macedo.abacatepay_spring_demo.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${abacatepay.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor bearerTokenInterceptor() {
        return template -> template.header("Authorization", "Bearer " + apiKey);
    }
}
