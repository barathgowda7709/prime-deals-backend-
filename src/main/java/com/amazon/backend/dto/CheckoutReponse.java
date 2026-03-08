package com.amazon.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CheckoutResponse {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal walletBalanceAfter;
    private String status;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private List<CheckoutItemResponse> items;

    public static class CheckoutItemResponse {
        private String productName;
        private int quantity;
        private BigDecimal price;
        private BigDecimal subtotal;

        public String getProductName() { return productName; }
        public void setProductName(String p) { this.productName = p; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int q) { this.quantity = q; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal p) { this.price = p; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal s) { this.subtotal = s; }
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long o) { this.orderId = o; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String o) { this.orderNumber = o; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal t) { this.totalAmount = t; }
    public BigDecimal getWalletBalanceAfter() { return walletBalanceAfter; }
    public void setWalletBalanceAfter(BigDecimal w) { this.walletBalanceAfter = w; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String d) { this.deliveryAddress = d; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public List<CheckoutItemResponse> getItems() { return items; }
    public void setItems(List<CheckoutItemResponse> i) { this.items = i; }
}