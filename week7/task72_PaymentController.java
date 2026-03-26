package com.example.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Payment Request DTO ───────────────────────────────────────
class PaymentRequest {
    private int orderId;
    private double amount;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

// ── Payment Response DTO ──────────────────────────────────────
class PaymentResponse {
    private int orderId;
    private String status;
    private String message;

    public PaymentResponse(int orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }
    public int getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}

// ── Payment Controller ────────────────────────────────────────
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // Called by Order Service to process payment
    // POST http://localhost:8081/api/payments/process
    // Body: { "orderId": 1, "amount": 75000 }
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {

        // Dummy logic: amounts above 0 succeed
        if (request.getAmount() > 0) {
            return ResponseEntity.ok(new PaymentResponse(
                    request.getOrderId(),
                    "SUCCESS",
                    "Payment of Rs." + request.getAmount() + " processed successfully"
            ));
        }

        return ResponseEntity.ok(new PaymentResponse(
                request.getOrderId(),
                "FAILED",
                "Invalid payment amount"
        ));
    }
}
