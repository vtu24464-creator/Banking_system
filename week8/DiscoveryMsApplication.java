package com.example.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Task 8.1 - Eureka Server (Discovery-MS)
 * Annotated with @EnableEurekaServer to act as the service registry.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryMsApplication.class, args);
    }
}
