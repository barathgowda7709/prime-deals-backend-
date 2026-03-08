package com.amazon.backend.dto;

import com.amazon.backend.model.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;
    private OrderStatus status;
    private Double totalPrice;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private String buyerName;
    private String buyerEmail;

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private String imageUrl;
        private Integer quantity;
        private Double priceAtPurchase;
        private Double subtotal;
    }
}
