package com.amazon.backend.controller;

import com.amazon.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final SellerService sellerService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkDelivery(
            @RequestParam Long productId,
            @RequestParam String pincode) {
        return ResponseEntity.ok(sellerService.checkDelivery(productId, pincode));
    }
}
