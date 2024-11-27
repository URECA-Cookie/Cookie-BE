package com.cookie.domain.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {
    private String content;
    private Integer movieScore;
    private Boolean isSpoiler;
}