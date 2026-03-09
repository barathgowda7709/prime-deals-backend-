package com.amazon.backend.service;

import com.amazon.backend.dto.*;
import com.amazon.backend.model.*;
import com.amazon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SellerPincodeRepository sellerPincodeRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isSeller(String email) {
        User user = getUser(email);
        return sellerRepository.existsByUser(user);
    }

    public SellerResponse registerAsSeller(String email, SellerRequest req) {
        User user = getUser(email);
        if (sellerRepository.existsByUser(user))
            throw new RuntimeException("Already registered as seller");

        Seller seller = Seller.builder()
                .user(user)
                .shopName(req.getShopName())
                .businessType(req.getBusinessType())
                .businessAddress(req.getBusinessAddress())
                .gstNumber(req.getGstNumber())
                .panNumber(req.getPanNumber())
                .bankAccount(req.getBankAccount())
                .ifscCode(req.getIfscCode())
                .phone(req.getPhone())
                .status("ACTIVE")
                .build();

        return toResponse(sellerRepository.save(seller));
    }

    public SellerResponse getProfile(String email) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        return toResponse(seller);
    }

    public SellerResponse updateProfile(String email, SellerRequest req) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        if (req.getShopName() != null) seller.setShopName(req.getShopName());
        if (req.getBusinessType() != null) seller.setBusinessType(req.getBusinessType());
        if (req.getBusinessAddress() != null) seller.setBusinessAddress(req.getBusinessAddress());
        if (req.getGstNumber() != null) seller.setGstNumber(req.getGstNumber());
        if (req.getPanNumber() != null) seller.setPanNumber(req.getPanNumber());
        if (req.getBankAccount() != null) seller.setBankAccount(req.getBankAccount());
        if (req.getIfscCode() != null) seller.setIfscCode(req.getIfscCode());
        if (req.getPhone() != null) seller.setPhone(req.getPhone());
        return toResponse(sellerRepository.save(seller));
    }

    // Seller's products
    public List<ProductResponse> getMyProducts(String email) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        return productRepository.findBySellerId(seller.getId())
                .stream().map(this::toProductResponse).collect(Collectors.toList());
    }

    public ProductResponse addProduct(String email, ProductRequest req) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        Product p = Product.builder()
                .name(req.getName()).description(req.getDescription())
                .price(req.getPrice()).stock(req.getStock())
                .category(req.getCategory()).brand(req.getBrand())
                .imageUrl(req.getImageUrl()).sellerId(seller.getId())
                .rating(0.0).numReviews(0)
                .build();
        return toProductResponse(productRepository.save(p));
    }

    public ProductResponse updateProduct(String email, Long productId, ProductRequest req) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!seller.getId().equals(p.getSellerId()))
            throw new RuntimeException("Not your product");
        if (req.getName() != null) p.setName(req.getName());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getStock() != null) p.setStock(req.getStock());
        if (req.getCategory() != null) p.setCategory(req.getCategory());
        if (req.getBrand() != null) p.setBrand(req.getBrand());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        return toProductResponse(productRepository.save(p));
    }

    public void deleteProduct(String email, Long productId) {
        User user = getUser(email);
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!seller.getId().equals(p.getSellerId()))
            throw new RuntimeException("Not your product");
        productRepository.deleteById(productId);
    }

    // ─── PINCODE MANAGEMENT ──────────────────────────────────────────────────

    private Seller getSeller(String email) {
        User user = getUser(email);
        return sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Not a seller"));
    }

    public List<Map<String, Object>> getMyPincodes(String email) {
        Seller seller = getSeller(email);
        return sellerPincodeRepository.findBySeller(seller).stream()
                .map(sp -> Map.<String, Object>of(
                        "id", sp.getId(),
                        "pincode", sp.getPincode(),
                        "deliveryDays", sp.getDeliveryDays()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> addPincode(String email, String pincode, Integer deliveryDays) {
        Seller seller = getSeller(email);
        if (sellerPincodeRepository.findBySellerAndPincode(seller, pincode).isPresent())
            throw new RuntimeException("Pincode already exists");
        SellerPincode sp = SellerPincode.builder()
                .seller(seller).pincode(pincode).deliveryDays(deliveryDays).build();
        sp = sellerPincodeRepository.save(sp);
        return Map.of("id", sp.getId(), "pincode", sp.getPincode(), "deliveryDays", sp.getDeliveryDays());
    }

    public void deletePincode(String email, Long pincodeId) {
        Seller seller = getSeller(email);
        SellerPincode sp = sellerPincodeRepository.findById(pincodeId)
                .orElseThrow(() -> new RuntimeException("Pincode not found"));
        if (!sp.getSeller().getId().equals(seller.getId()))
            throw new RuntimeException("Not your pincode");
        sellerPincodeRepository.deleteById(pincodeId);
    }

    // ─── DELIVERY CHECK ───────────────────────────────────────────────────────

    public Map<String, Object> checkDelivery(Long productId, String pincode) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getSellerId() == null)
            return Map.of("deliverable", false, "message", "No seller assigned");

        return sellerPincodeRepository.findBySellerIdAndPincode(product.getSellerId(), pincode)
                .map(sp -> Map.<String, Object>of(
                        "deliverable", true,
                        "deliveryDays", sp.getDeliveryDays(),
                        "message", "Delivers in " + sp.getDeliveryDays() + " day" + (sp.getDeliveryDays() == 1 ? "" : "s")))
                .orElse(Map.of("deliverable", false, "message", "Not serviceable to this pincode"));
    }

    private SellerResponse toResponse(Seller s) {
        SellerResponse r = new SellerResponse();
        r.setId(s.getId());
        r.setShopName(s.getShopName());
        r.setBusinessType(s.getBusinessType());
        r.setBusinessAddress(s.getBusinessAddress());
        r.setGstNumber(s.getGstNumber());
        r.setPanNumber(s.getPanNumber());
        r.setBankAccount(s.getBankAccount());
        r.setIfscCode(s.getIfscCode());
        r.setPhone(s.getPhone());
        r.setStatus(s.getStatus());
        r.setOwnerName(s.getUser().getName());
        r.setOwnerEmail(s.getUser().getEmail());
        r.setCreatedAt(s.getCreatedAt());
        return r;
    }

    private ProductResponse toProductResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId()); r.setName(p.getName()); r.setDescription(p.getDescription());
        r.setPrice(p.getPrice()); r.setStock(p.getStock()); r.setCategory(p.getCategory());
        r.setBrand(p.getBrand()); r.setImageUrl(p.getImageUrl());
        r.setRating(p.getRating()); r.setNumReviews(p.getNumReviews());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
