package com.amazon.backend.repository;

import com.amazon.backend.model.Seller;
import com.amazon.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUser(User user);
    boolean existsByUser(User user);
}
