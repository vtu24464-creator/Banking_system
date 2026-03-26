package com.example.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// ── Eureka Server ────────────────────────────────────────────
// This is the Service Registry.
// All microservices register here on startup.
// Other services discover each other through this registry
// instead of using hardcoded IP addresses.
//
// Access Eureka Dashboard at: http://localhost:8761
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
