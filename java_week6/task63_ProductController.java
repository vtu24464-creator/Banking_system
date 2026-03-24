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

    // GET all products
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    // POST add product
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        product.setId(idCounter++);
        products.add(product);
        return product;
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT update product → PUT http://localhost:8080/api/products/1
    // Body: { "name": "Gaming Laptop", "price": 95000.00 }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id,
                                                  @RequestBody Product updated) {
        for (Product p : products) {
            if (p.getId() == id) {
                p.setName(updated.getName());
                p.setPrice(updated.getPrice());
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE product → DELETE http://localhost:8080/api/products/1
    // Returns: "Product with ID 1 deleted successfully"
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            return ResponseEntity.ok("Product with ID " + id + " deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
