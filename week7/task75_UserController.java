package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ─────────────────────────────────────────────────────────────────
// Task 7.5 — User Service (split from monolith)
//
// Microservices Design Principles Applied:
//  ✔ Single Responsibility — handles ONLY user operations
//  ✔ Loose Coupling        — no direct dependency on Product Service
//  ✔ Independent Deployment— runs on its own port (8081) with its own DB
//  ✔ Decentralized Data    — owns user_db exclusively
//
// Runs on: http://localhost:8081
// Database: MySQL → user_db
// ─────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // POST /api/users — Register a new user
    // Body: { "name": "Arun", "email": "arun@gmail.com", "password": "pass123" }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/users — Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // GET /api/users/1 — Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/users/1 — Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id,
                                           @RequestBody User updated) {
        return userRepository.findById(id).map(user -> {
            user.setName(updated.getName());
            user.setEmail(updated.getEmail());
            user.setPassword(updated.getPassword());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/1 — Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
