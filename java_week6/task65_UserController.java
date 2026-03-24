package com.example.auth.controller;

import com.example.auth.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    // POST /register
    // Valid Request Body:  { "name": "Arun", "email": "arun@gmail.com", "age": 22 }
    // Invalid Body sample: { "name": "", "email": "notanemail", "age": 15 }
    //   → 400 Bad Request with structured error JSON from GlobalExceptionHandler
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        return ResponseEntity.ok("User registered successfully: " + user.getName());
    }
}
