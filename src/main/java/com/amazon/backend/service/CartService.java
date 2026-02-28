package com.amazon.backend.service;

import com.amazon.backend.dto.CartItemRequest;
import com.amazon.backend.dto.CartResponse;
import com.amazon.backend.model.CartItem;
import com.amazon.backend.model.Product;
import com.amazon.backend.model.User;
import com.amazon.backend.repository.CartRepository;
import com.amazon.backend.repository.ProductRepository;
import com.amazon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponse getCart(String email) {
        User user = getUser(email);
        List<CartItem> items = cartRepository.findByUser(user);
        return buildCartResponse(items);
    }

    public CartResponse addToCart(String email, CartItemRequest request) {
        User user = getUser(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartRepository.findByUserAndProductId(user, request.getProductId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartRepository.save(cartItem);

        return buildCartResponse(cartRepository.findByUser(user));
    }

    public CartResponse removeFromCart(String email, Long productId) {
        User user = getUser(email);
        CartItem cartItem = cartRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new RuntimeException("Item not in cart"));
        cartRepository.delete(cartItem);
        return buildCartResponse(cartRepository.findByUser(user));
    }

    public void clearCart(String email) {
        User user = getUser(email);
        cartRepository.deleteByUser(user);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CartResponse buildCartResponse(List<CartItem> items) {
        List<CartResponse.CartItemResponse> itemResponses = items.stream().map(item -> {
            CartResponse.CartItemResponse r = new CartResponse.CartItemResponse();
            r.setCartItemId(item.getId());
            r.setProductId(item.getProduct().getId());
            r.setProductName(item.getProduct().getName());
            r.setImageUrl(item.getProduct().getImageUrl());
            r.setPrice(item.getProduct().getPrice());
            r.setQuantity(item.getQuantity());
            r.setSubtotal(item.getProduct().getPrice() * item.getQuantity());
            return r;
        }).collect(Collectors.toList());

        Double total = itemResponses.stream()
                .mapToDouble(CartResponse.CartItemResponse::getSubtotal)
                .sum();

        CartResponse response = new CartResponse();
        response.setItems(itemResponses);
        response.setTotalPrice(total);
        return response;
    }
}