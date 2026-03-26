package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.model.PaymentRequest;
import com.example.order.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    // Payment Service URL (runs on port 8081)
    private static final String PAYMENT_SERVICE_URL = "http://localhost:8081/api/payments/process";

    private List<Order> orders = new ArrayList<>();
    private int idCounter = 1;

    // ── Place an order and trigger payment ──────────────────────────────────
    // POST http://localhost:8080/api/orders
    // Body: { "userId": 1, "product": "Laptop", "amount": 75000 }
    //
    // INTERACTION STEPS:
    //  1. Client sends POST /api/orders to Order Service
    //  2. Order Service creates order with status PENDING
    //  3. Order Service sends PaymentRequest to Payment Service (POST /api/payments/process)
    //  4. Payment Service processes and returns PaymentResponse (SUCCESS/FAILED)
    //  5. Order Service updates order status based on payment result
    //  6. Order Service returns final order to client
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {

        // Step 1: Create order with PENDING status
        order.setOrderId(idCounter++);
        order.setStatus("PENDING");
        orders.add(order);

        // Step 2: Build payment request
        PaymentRequest paymentRequest = new PaymentRequest(order.getOrderId(), order.getAmount());

        // Step 3: Call Payment Service via REST API
        PaymentResponse paymentResponse = restTemplate.postForObject(
                PAYMENT_SERVICE_URL,
                paymentRequest,
                PaymentResponse.class
        );

        // Step 4: Update order status based on payment result
        if (paymentResponse != null && "SUCCESS".equals(paymentResponse.getStatus())) {
            order.setStatus("CONFIRMED");
        } else {
            order.setStatus("FAILED");
        }

        return ResponseEntity.ok(order);
    }

    // GET all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orders;
    }

    // GET order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        return orders.stream()
                .filter(o -> o.getOrderId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
