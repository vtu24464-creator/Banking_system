package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Task 8.3 - Spring Cloud API Gateway
 * Registered as a Eureka client, routes requests to User-MS and AccountMng-MS.
 * Supports both Manual Routing and Automatic Routing (lb:// prefix).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMsApplication.class, args);
    }
}
