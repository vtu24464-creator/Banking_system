package com.example.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Task 8.1 & 8.2 - User Microservice (Eureka Client)
 * Registers itself with Eureka Server on startup.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMsApplication.class, args);
    }
}
