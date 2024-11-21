package com.cookie.domain.review.dto.response;

import com.cookie.domain.movie.dto.response.ReviewMovieResponse;
import com.cookie.domain.user.dto.response.ReviewUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResponse {
    private String content;
    private double movieScore;
    private long reviewLike;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReviewMovieResponse movie;
    private ReviewUserResponse user;
    private List<ReviewCommentResponse> comments;

}
