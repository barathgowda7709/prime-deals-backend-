package com.amazon.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "shipping_addresses")
@Data
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fullName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country = "India";

    private Boolean isDefault = false;
}
