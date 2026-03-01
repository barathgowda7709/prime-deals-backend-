package com.amazon.backend.service;

import com.amazon.backend.dto.OrderResponse;
import com.amazon.backend.dto.ProductRequest;
import com.amazon.backend.dto.ProductResponse;
import com.amazon.backend.model.Order;
import com.amazon.backend.model.OrderItem;
import com.amazon.backend.model.OrderStatus;
import com.amazon.backend.model.Product;
import com.amazon.backend.repository.OrderRepository;
import com.amazon.backend.repository.ProductRepository;
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

        long totalOrders = allOrders.size();
        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        long totalProducts = productRepository.count();
        long totalUsers = userRepository.count();
        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();
        long deliveredOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalProducts", totalProducts);
        stats.put("totalUsers", totalUsers);
        stats.put("pendingOrders", pendingOrders);
        stats.put("deliveredOrders", deliveredOrders);
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
        return response;
    }
}
