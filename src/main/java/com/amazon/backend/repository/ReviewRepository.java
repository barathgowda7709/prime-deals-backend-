package com.amazon.backend.repository;

import com.amazon.backend.model.Review;
import com.amazon.backend.model.Product;
import com.amazon.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    Optional<Review> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double findAverageRatingByProduct(Product product);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product")
    Long countByProduct(Product product);
}
