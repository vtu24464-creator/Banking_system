package com.example.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Task 8.1 & 8.2 - Account Management Microservice (Eureka Client)
 * Registers itself with Eureka Server on startup.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AccountMngMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountMngMsApplication.class, args);
    }
}
