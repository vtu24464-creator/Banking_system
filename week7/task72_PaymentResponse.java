package com.example.order.model;

public class PaymentResponse {
    private int orderId;
    private String status;   // SUCCESS or FAILED
    private String message;

    public PaymentResponse() {}
    public PaymentResponse(int orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
