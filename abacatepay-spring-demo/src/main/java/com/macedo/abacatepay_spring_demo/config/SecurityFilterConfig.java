package com.macedo.abacatepay_spring_demo.config;

import com.macedo.abacatepay_spring_demo.filter.HmacSignatureFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityFilterConfig {

    @Bean
    public FilterRegistrationBean<HmacSignatureFilter> hmacFilter(HmacSignatureFilter filter) {
        FilterRegistrationBean<HmacSignatureFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/payment/webhook");
        registration.setOrder(1);
        return registration;
    }
}
