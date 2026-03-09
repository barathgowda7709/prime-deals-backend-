package com.amazon.backend.dto;

import lombok.Data;

@Data
public class SellerRequest {
    private String shopName;
    private String businessType;
    private String businessAddress;
    private String gstNumber;
    private String panNumber;
    private String bankAccount;
    private String ifscCode;
    private String phone;
}
