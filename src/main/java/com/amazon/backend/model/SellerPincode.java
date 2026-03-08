package com.amazon.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "seller_pincodes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"seller_id", "pincode"}))
public class SellerPincode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, length = 6)
    private String pincode;

    @Column(nullable = false)
    private Integer deliveryDays;
}
