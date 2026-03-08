package com.amazon.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "sellers")
public class Seller {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String shopName;

    private String businessType;   // SOLE_PROPRIETOR, PARTNERSHIP, PVT_LTD, LLP
    private String businessAddress;
    private String gstNumber;
    private String panNumber;
    private String bankAccount;
    private String ifscCode;
    private String phone;

    @Column(nullable = false)
    private String status = "ACTIVE"; // PENDING, ACTIVE, SUSPENDED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "ACTIVE";
    }
}
