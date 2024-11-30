package com.cookie.domain.movie.dto.response;

import com.cookie.domain.review.dto.response.ReviewResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MoviePagenationResponse {
    private Integer currentPage;
    private List<MovieSimpleResponse> movies;
    private Integer totalPages;
}
