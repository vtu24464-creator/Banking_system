package com.example.productapi.controller;

import com.example.productapi.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<Product> products = new ArrayList<>();
    private int idCounter = 1;

    // GET all products → http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    // POST add new product → http://localhost:8080/api/products
    // Body: { "name": "Laptop", "price": 75000.00 }
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        product.setId(idCounter++);
        products.add(product);
        return product;
    }
}
