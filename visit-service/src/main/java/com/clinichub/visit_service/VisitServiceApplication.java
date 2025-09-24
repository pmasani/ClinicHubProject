package com.clinichub.visit_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VisitServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisitServiceApplication.class, args);
    }
}
