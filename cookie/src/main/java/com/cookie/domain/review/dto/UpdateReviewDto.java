package com.cookie.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewDto {
    private String content;
    private double movieScore;
    private Boolean isSpoiler;
}