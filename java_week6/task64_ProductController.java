package com.example.productapi.controller;

import com.example.productapi.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<Product> products = new ArrayList<>();
    private int idCounter = 1;

    // ─── @RequestBody + custom 201 Created ──────────────────────────────────
    // POST http://localhost:8080/api/products
    // Body: { "name": "Tablet", "price": 25000 }
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        product.setId(idCounter++);
        products.add(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product); // 201
    }

    // ─── @PathVariable + custom 200/404 ─────────────────────────────────────
    // GET http://localhost:8080/api/products/1
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(p -> ResponseEntity.status(HttpStatus.OK).body(p))         // 200
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());   // 404
    }

    // ─── @RequestParam + custom 200/404 ─────────────────────────────────────
    // GET http://localhost:8080/api/products/search?name=Laptop
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        List<Product> result = products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);    // 404
        }
        return ResponseEntity.ok(result);                                        // 200
    }

    // ─── GET all ─────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(products);
    }

    // ─── PUT update ──────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id,
                                                  @RequestBody Product updated) {
        for (Product p : products) {
            if (p.getId() == id) {
                p.setName(updated.getName());
                p.setPrice(updated.getPrice());
                return ResponseEntity.ok(p);                                     // 200
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();              // 404
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();         // 204
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();              // 404
    }
}
