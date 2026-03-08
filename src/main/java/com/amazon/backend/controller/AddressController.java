package com.amazon.backend.controller;

import com.amazon.backend.dto.AddressRequest;
import com.amazon.backend.dto.AddressResponse;
import com.amazon.backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Get all addresses
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getAddresses(userDetails.getUsername()));
    }

    // Add new address
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(userDetails.getUsername(), request));
    }

    // Set address as default
    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefault(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefault(userDetails.getUsername(), addressId));
    }

    // Delete address
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }
}
