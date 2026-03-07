package com.amazon.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String brand;
    private Double price;
    private String imageUrl;
    private String category;
    private Integer stock;
    private LocalDateTime addedAt;
}
