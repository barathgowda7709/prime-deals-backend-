package com.amazon.backend.service;

import com.amazon.backend.dto.OrderResponse;
import com.amazon.backend.dto.ProductRequest;
import com.amazon.backend.dto.ProductResponse;
import com.amazon.backend.model.*;
import com.amazon.backend.repository.OrderRepository;
import com.amazon.backend.repository.ProductRepository;
import com.amazon.backend.repository.SellerRepository;
import com.amazon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    // ─── USERS ────────────────────────────────────────────────────────────────

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id",        u.getId());
            m.put("name",      u.getName());
            m.put("email",     u.getEmail());
            m.put("role",      u.getRole().name());
            m.put("phone",     u.getPhone());
            m.put("createdAt", u.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ─── SELLERS ──────────────────────────────────────────────────────────────

    public List<Map<String, Object>> getAllSellers() {
        return sellerRepository.findAll().stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id",           s.getId());
            m.put("shopName",     s.getShopName());
            m.put("businessType", s.getBusinessType());
            m.put("gstNumber",    s.getGstNumber());
            m.put("panNumber",    s.getPanNumber());
            m.put("phone",        s.getPhone());
            m.put("status",       s.getStatus());
            m.put("createdAt",    s.getCreatedAt());
            m.put("userName",     s.getUser().getName());
            m.put("userEmail",    s.getUser().getEmail());
            m.put("userId",       s.getUser().getId());
            return m;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> updateSellerStatus(Long sellerId, String status) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        seller.setStatus(status);
        sellerRepository.save(seller);
        Map<String, Object> m = new HashMap<>();
        m.put("id",     seller.getId());
        m.put("status", seller.getStatus());
        return m;
    }

    // ─── PRODUCTS ─────────────────────────────────────────────────────────────

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .brand(request.getBrand())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .stock(request.getStock())
                .rating(0.0)
                .numReviews(0)
                .build();
        return buildProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        return buildProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return buildOrderResponse(orderRepository.save(order));
    }

    // ─── DASHBOARD ────────────────────────────────────────────────────────────

    public Map<String, Object> getDashboardStats() {
        List<Order> allOrders = orderRepository.findAll();

        long totalOrders      = allOrders.size();
        double totalRevenue   = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getTotalPrice).sum();
        long totalProducts    = productRepository.count();
        long totalUsers       = userRepository.count();
        long totalSellers     = sellerRepository.count();
        long pendingOrders    = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long deliveredOrders  = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelledOrders  = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders",     totalOrders);
        stats.put("totalRevenue",    totalRevenue);
        stats.put("totalProducts",   totalProducts);
        stats.put("totalUsers",      totalUsers);
        stats.put("totalSellers",    totalSellers);
        stats.put("pendingOrders",   pendingOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("cancelledOrders", cancelledOrders);
        return stats;
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private ProductResponse buildProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setBrand(product.getBrand());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setStock(product.getStock());
        response.setRating(product.getRating());
        response.setNumReviews(product.getNumReviews());
        return response;
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            OrderResponse.OrderItemResponse r = new OrderResponse.OrderItemResponse();
            r.setProductId(item.getProduct().getId());
            r.setProductName(item.getProduct().getName());
            r.setImageUrl(item.getProduct().getImageUrl());
            r.setQuantity(item.getQuantity());
            r.setPriceAtPurchase(item.getPriceAtPurchase());
            r.setSubtotal(item.getPriceAtPurchase() * item.getQuantity());
            return r;
        }).collect(Collectors.toList());

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setShippingAddress(order.getShippingAddress());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(itemResponses);

        // include buyer info
        if (order.getUser() != null) {
            response.setBuyerName(order.getUser().getName());
            response.setBuyerEmail(order.getUser().getEmail());
        }
        return response;
    }
}
