package com.amazon.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String userName;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
}
