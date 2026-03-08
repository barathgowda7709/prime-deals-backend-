package com.amazon.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    private String title;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String comment;
}
