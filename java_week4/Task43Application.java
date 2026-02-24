package com.week4.task43;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Task43Application {
    public static void main(String[] args) {
        SpringApplication.run(Task43Application.class, args);
        System.out.println("Task 4.3 — Constructor Injection started on port 8085");
        System.out.println("   Test: GET http://localhost:8085/payment/process/500.00");
    }
}

interface PaymentService {
    String processPayment(double amount);

    String getPaymentStatus();
}

@Service
class PaymentServiceImpl implements PaymentService {

    @Override
    public String processPayment(double amount) {
        if (amount <= 0) {
            return "Invalid payment amount: " + amount;
        }
        return "Payment of ₹" + amount + " processed successfully via PaymentServiceImpl.";
    }

    @Override
    public String getPaymentStatus() {
        return "Payment Gateway: ONLINE | Mode: UPI | Status: ACTIVE";
    }
}

@RestController
@RequestMapping("/payment")
class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
        System.out.println("PaymentController created — PaymentService injected via constructor.");
    }

    @GetMapping("/process/{amount}")
    public String process(@PathVariable double amount) {
        return paymentService.processPayment(amount);
    }

    @GetMapping("/status")
    public String status() {
        return paymentService.getPaymentStatus();
    }
}
