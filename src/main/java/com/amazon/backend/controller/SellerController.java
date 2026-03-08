package com.amazon.backend.controller;

import com.amazon.backend.dto.*;
import com.amazon.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> isSeller(Authentication auth) {
        return ResponseEntity.ok(Map.of("isSeller", sellerService.isSeller(auth.getName())));
    }

    @PostMapping("/register")
    public ResponseEntity<SellerResponse> register(@RequestBody SellerRequest req, Authentication auth) {
        return ResponseEntity.ok(sellerService.registerAsSeller(auth.getName(), req));
    }

    @GetMapping("/profile")
    public ResponseEntity<SellerResponse> profile(Authentication auth) {
        return ResponseEntity.ok(sellerService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<SellerResponse> updateProfile(@RequestBody SellerRequest req, Authentication auth) {
        return ResponseEntity.ok(sellerService.updateProfile(auth.getName(), req));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> myProducts(Authentication auth) {
        return ResponseEntity.ok(sellerService.getMyProducts(auth.getName()));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest req, Authentication auth) {
        return ResponseEntity.ok(sellerService.addProduct(auth.getName(), req));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req, Authentication auth) {
        return ResponseEntity.ok(sellerService.updateProduct(auth.getName(), id, req));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id, Authentication auth) {
        sellerService.deleteProduct(auth.getName(), id);
        return ResponseEntity.ok("Deleted");
    }
}
