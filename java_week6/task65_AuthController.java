package com.example.auth.controller;

import com.example.auth.model.LoginRequest;
import com.example.auth.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    // POST /login
    // Request Body: { "username": "admin", "password": "password123" }
    // Success → 200 OK with JWT token
    // Failure → 401 Unauthorized with error message
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // Dummy credential check (replace with DB lookup in production)
        if ("admin".equals(request.getUsername())
                && "password123".equals(request.getPassword())) {

            String token = JwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }
}
