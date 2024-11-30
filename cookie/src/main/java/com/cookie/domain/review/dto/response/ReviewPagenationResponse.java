package com.cookie.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewPagenationResponse {
    private Integer currentPage;
    private List<ReviewResponse> reviews;
    private Integer totalPages;
}
