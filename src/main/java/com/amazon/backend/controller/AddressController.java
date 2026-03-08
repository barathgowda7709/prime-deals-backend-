package com.amazon.backend.controller;

import com.amazon.backend.dto.AddressRequest;
import com.amazon.backend.dto.AddressResponse;
import com.amazon.backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(Authentication authentication) {
        return ResponseEntity.ok(addressService.getAddresses(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(authentication.getName(), request));
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefault(
            Authentication authentication,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefault(authentication.getName(), addressId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId) {
        addressService.deleteAddress(authentication.getName(), addressId);
        return ResponseEntity.noContent().build();
    }
}
