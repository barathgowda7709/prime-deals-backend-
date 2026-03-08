package com.amazon.backend.service;

import com.amazon.backend.dto.CheckoutRequest;
import com.amazon.backend.dto.CheckoutResponse;
import com.amazon.backend.model.*;
import com.amazon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        BigDecimal total = cartItems.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (user.getWalletBalance().compareTo(total) < 0)
            throw new RuntimeException("Insufficient wallet balance. Required: ₹" + total
                    + ", Available: ₹" + user.getWalletBalance());

        Address address;
        if (request.getAddressId() != null) {
            address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
        } else {
            address = addressRepository.findByUserAndIsDefaultTrue(user)
                    .orElseThrow(() -> new RuntimeException("No delivery address found"));
        }

        user.setWalletBalance(user.getWalletBalance().subtract(total));
        userRepository.save(user);

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(total);
        order.setDeliveryAddress(address.getFullName() + ", " + address.getStreet()
                + ", " + address.getCity() + ", " + address.getState()
                + " - " + address.getPinCode());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        List<CheckoutResponse.CheckoutItemResponse> responseItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity())
                throw new RuntimeException("Insufficient stock for: " + product.getName());

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            CheckoutResponse.CheckoutItemResponse ri = new CheckoutResponse.CheckoutItemResponse();
            ri.setProductName(product.getName());
            ri.setQuantity(cartItem.getQuantity());
            ri.setPrice(product.getPrice());
            ri.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            responseItems.add(ri);
        }

        order.setItems(orderItems);
        Order saved = orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        CheckoutResponse response = new CheckoutResponse();
        response.setOrderId(saved.getId());
        response.setOrderNumber(saved.getOrderNumber());
        response.setTotalAmount(total);
        response.setWalletBalanceAfter(user.getWalletBalance());
        response.setStatus("CONFIRMED");
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setCreatedAt(saved.getCreatedAt());
        response.setItems(responseItems);
        return response;
    }

    public BigDecimal getWalletBalance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWalletBalance();
    }
}