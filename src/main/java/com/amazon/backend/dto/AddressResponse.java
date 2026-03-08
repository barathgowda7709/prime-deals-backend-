package com.amazon.backend.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private Boolean isDefault;
}
