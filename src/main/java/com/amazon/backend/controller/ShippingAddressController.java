package com.amazon.backend.controller;

import com.amazon.backend.dto.ShippingAddressRequest;
import com.amazon.backend.dto.ShippingAddressResponse;
import com.amazon.backend.service.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShippingAddressController {

    private final ShippingAddressService addressService;

    // GET /api/addresses — get all my addresses
    @GetMapping
    public ResponseEntity<List<ShippingAddressResponse>> getMyAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }

    // POST /api/addresses — add new address
    @PostMapping
    public ResponseEntity<ShippingAddressResponse> addAddress(@Valid @RequestBody ShippingAddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    // PUT /api/addresses/{id} — update address
    @PutMapping("/{id}")
    public ResponseEntity<ShippingAddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody ShippingAddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }

    // DELETE /api/addresses/{id} — delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Address deleted");
    }

    // PUT /api/addresses/{id}/default — set as default
    @PutMapping("/{id}/default")
    public ResponseEntity<ShippingAddressResponse> setDefault(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefault(id));
    }
}
