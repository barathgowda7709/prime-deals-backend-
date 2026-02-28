package com.amazon.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String brand;
    private String imageUrl;
    private Double rating;
    private Integer numReviews;
    private LocalDateTime createdAt;
}