package com.amazon.backend.controller;

import com.amazon.backend.dto.CheckoutRequest;
import com.amazon.backend.dto.CheckoutResponse;
import com.amazon.backend.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(checkoutService.checkout(request));
    }

    @GetMapping("/wallet")
    public ResponseEntity<Map<String, BigDecimal>> getWalletBalance() {
        return ResponseEntity.ok(Map.of("balance", checkoutService.getWalletBalance()));
    }
}