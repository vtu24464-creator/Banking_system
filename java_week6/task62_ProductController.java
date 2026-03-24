package com.example.productapi.controller;

import com.example.productapi.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<Product> products = new ArrayList<>();
    private int idCounter = 1;

    // GET all products → GET http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    // POST add product → POST http://localhost:8080/api/products
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        product.setId(idCounter++);
        products.add(product);
        return product;
    }

    // GET product by ID → GET http://localhost:8080/api/products/1
    // Returns 200 OK with product JSON, or 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
