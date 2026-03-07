package com.amazon.backend.controller;

import com.amazon.backend.dto.WishlistItemResponse;
import com.amazon.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // GET /api/wishlist
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getMyWishlist());
    }

    // POST /api/wishlist/add/{productId}
    @PostMapping("/add/{productId}")
    public ResponseEntity<WishlistItemResponse> addToWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(productId));
    }

    // DELETE /api/wishlist/remove/{productId}
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok("Removed from wishlist");
    }

    // GET /api/wishlist/check/{productId}
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(@PathVariable Long productId) {
        boolean inWishlist = wishlistService.isInWishlist(productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }

    // DELETE /api/wishlist/clear
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearWishlist() {
        wishlistService.clearWishlist();
        return ResponseEntity.ok("Wishlist cleared");
    }
}
