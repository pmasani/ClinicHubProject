package com.clinichub.visit_service.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignNoRetryConfig {
    @Bean
    public Retryer retryer() {
        // It tells Feign that when it attempts a call to any service using this configuration, it should never automatically retry the request if the first attempt fails
        return Retryer.NEVER_RETRY;
    }
}



