package com.example.product.controller;

import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ─────────────────────────────────────────────────────────────────
// Task 7.5 — Product Service (split from monolith)
//
// Microservices Design Principles Applied:
//  ✔ Single Responsibility — handles ONLY product operations
//  ✔ Loose Coupling        — no direct dependency on User Service
//  ✔ Independent Deployment— runs on its own port (8082) with its own DB
//  ✔ Decentralized Data    — owns product_db exclusively
//
// Runs on: http://localhost:8082
// Database: MySQL → product_db
// ─────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // POST /api/products — Add a new product
    // Body: { "name": "Laptop", "category": "Electronics", "price": 75000, "stock": 10 }
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/products — Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // GET /api/products/1 — Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/products/category/Electronics — Filter by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productRepository.findByCategory(category));
    }

    // PUT /api/products/1 — Update product details
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id,
                                                  @RequestBody Product updated) {
        return productRepository.findById(id).map(product -> {
            product.setName(updated.getName());
            product.setCategory(updated.getCategory());
            product.setPrice(updated.getPrice());
            product.setStock(updated.getStock());
            return ResponseEntity.ok(productRepository.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/1 — Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
