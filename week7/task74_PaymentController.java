package com.example.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private List<Map<String, Object>> payments = new ArrayList<>();
    private int idCounter = 1;

    // Process a payment
    // POST http://localhost:8084/api/payments/process
    // Body: { "orderId": 1, "amount": 5000.0 }
    @PostMapping("/process")
    public Map<String, Object> processPayment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", idCounter++);
        response.put("orderId", request.get("orderId"));
        response.put("amount", request.get("amount"));
        response.put("status", "SUCCESS");
        response.put("message", "Payment processed successfully");
        payments.add(response);
        return response;
    }

    @GetMapping
    public List<Map<String, Object>> getAllPayments() {
        return payments;
    }
}
