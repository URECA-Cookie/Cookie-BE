package com.cookie.domain.review.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
    private List<ReviewResponse> reviews;
    private long totalReviews;
    private long totalReviewPages;
}
