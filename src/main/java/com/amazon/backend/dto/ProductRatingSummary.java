package com.amazon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRatingSummary {
    private Double averageRating;
    private Long totalReviews;
    private Long fiveStar;
    private Long fourStar;
    private Long threeStar;
    private Long twoStar;
    private Long oneStar;
}
