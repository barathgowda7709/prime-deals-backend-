package com.amazon.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private List<CartItemResponse> items;
    private Double totalPrice;

    @Data
    public static class CartItemResponse {
        private Long cartItemId;
        private Long productId;
        private String productName;
        private String imageUrl;
        private Double price;
        private Integer quantity;
        private Double subtotal;
    }
}