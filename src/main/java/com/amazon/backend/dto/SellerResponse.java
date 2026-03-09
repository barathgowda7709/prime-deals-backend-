package com.amazon.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SellerResponse {
    private Long id;
    private String shopName;
    private String businessType;
    private String businessAddress;
    private String gstNumber;
    private String panNumber;
    private String bankAccount;
    private String ifscCode;
    private String phone;
    private String status;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
}
