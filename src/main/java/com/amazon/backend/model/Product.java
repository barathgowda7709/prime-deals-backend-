package com.amazon.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    private String category;

    private String brand;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(name = "num_reviews")
    private Integer numReviews = 0;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.rating == null) this.rating = 0.0;
        if (this.numReviews == null) this.numReviews = 0;
    }
}