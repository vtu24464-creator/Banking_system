package com.example.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    // In-memory user store
    private List<Map<String, Object>> users = new ArrayList<>();
    private int idCounter = 1;

    // ── Register user ───────────────────────────────────────────────────────
    // POST http://localhost:8082/api/users
    // Body: { "name": "Arun", "email": "arun@gmail.com" }
    @PostMapping
    public Map<String, Object> register(@RequestBody Map<String, Object> user) {
        user.put("id", idCounter++);
        users.add(user);
        return user;
    }

    // ── Get all users ───────────────────────────────────────────────────────
    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return users;
    }

    // ── User Service discovers Product Service via Eureka ───────────────────
    // Instead of calling http://localhost:8083/api/products
    // we use the service name: http://product-service/api/products
    // Eureka resolves "product-service" to the actual host:port at runtime.
    //
    // GET http://localhost:8082/api/users/products
    @GetMapping("/products")
    public Object getProductsViaDiscovery() {
        // "product-service" is the spring.application.name of Product Service
        // @LoadBalanced RestTemplate resolves this name through Eureka
        String url = "http://product-service/api/products";
        return restTemplate.getForObject(url, Object.class);
    }
}
