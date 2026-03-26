package com.example.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<Map<String, Object>> products = new ArrayList<>();
    private int idCounter = 1;

    @PostMapping
    public Map<String, Object> addProduct(@RequestBody Map<String, Object> product) {
        product.put("id", idCounter++);
        products.add(product);
        return product;
    }

    @GetMapping
    public List<Map<String, Object>> getAllProducts() {
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
