package com.amazon.backend.service;

import com.amazon.backend.dto.WishlistItemResponse;
import com.amazon.backend.model.Product;
import com.amazon.backend.model.User;
import com.amazon.backend.model.WishlistItem;
import com.amazon.backend.repository.ProductRepository;
import com.amazon.backend.repository.UserRepository;
import com.amazon.backend.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private WishlistItemResponse toResponse(WishlistItem item) {
        WishlistItemResponse r = new WishlistItemResponse();
        r.setId(item.getId());
        r.setProductId(item.getProduct().getId());
        r.setProductName(item.getProduct().getName());
        r.setBrand(item.getProduct().getBrand());
        r.setPrice(item.getProduct().getPrice());
        r.setImageUrl(item.getProduct().getImageUrl());
        r.setCategory(item.getProduct().getCategory());
        r.setStock(item.getProduct().getStock());
        r.setAddedAt(item.getAddedAt());
        return r;
    }

    public List<WishlistItemResponse> getMyWishlist() {
        User user = getCurrentUser();
        return wishlistRepository.findByUserOrderByAddedAtDesc(user)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public WishlistItemResponse addToWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in wishlist");
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        return toResponse(wishlistRepository.save(item));
    }

    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isInWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return wishlistRepository.existsByUserAndProduct(user, product);
    }

    @Transactional
    public void clearWishlist() {
        User user = getCurrentUser();
        wishlistRepository.findByUserOrderByAddedAtDesc(user)
                .forEach(wishlistRepository::delete);
    }
}
