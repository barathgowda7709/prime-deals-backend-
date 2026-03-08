package com.amazon.backend.service;

import com.amazon.backend.dto.ReviewRequest;
import com.amazon.backend.dto.ReviewResponse;
import com.amazon.backend.model.Product;
import com.amazon.backend.model.Review;
import com.amazon.backend.model.User;
import com.amazon.backend.repository.ProductRepository;
import com.amazon.backend.repository.ReviewRepository;
import com.amazon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ReviewResponse> getProductReviews(Long productId) {
        Product product = getProduct(productId);
        return reviewRepository.findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse addReview(String email, Long productId, ReviewRequest request) {
        User user = getUser(email);
        Product product = getProduct(productId);

        // One review per user per product
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Update product average rating and review count
        updateProductRating(product);

        return buildResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(String email, Long reviewId, ReviewRequest request) {
        User user = getUser(email);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        updateProductRating(review.getProduct());

        return buildResponse(saved);
    }

    @Transactional
    public void deleteReview(String email, Long reviewId) {
        User user = getUser(email);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        Double avg = reviewRepository.findAverageRatingByProduct(product);
        Long count = reviewRepository.countByProduct(product);
        product.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        product.setNumReviews(count.intValue());
        productRepository.save(product);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private ReviewResponse buildResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserName(review.getUser().getName());
        response.setRating(review.getRating());
        response.setTitle(review.getTitle());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
