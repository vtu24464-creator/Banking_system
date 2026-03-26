package com.example.order.model;

public class Order {
    private int orderId;
    private int userId;
    private String product;
    private double amount;
    private String status; // PENDING, CONFIRMED, FAILED

    public Order() {}
    public Order(int orderId, int userId, String product, double amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.product = product;
        this.amount = amount;
        this.status = "PENDING";
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
