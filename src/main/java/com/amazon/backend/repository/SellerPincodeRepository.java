package com.amazon.backend.repository;

import com.amazon.backend.model.Seller;
import com.amazon.backend.model.SellerPincode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SellerPincodeRepository extends JpaRepository<SellerPincode, Long> {
    List<SellerPincode> findBySeller(Seller seller);
    Optional<SellerPincode> findBySellerAndPincode(Seller seller, String pincode);
    // Find any pincode entry for a seller by sellerId + pincode (for delivery check)
    Optional<SellerPincode> findBySellerIdAndPincode(Long sellerId, String pincode);
}
