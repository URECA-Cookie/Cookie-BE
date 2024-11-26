package com.cookie.domain.review.dto.response;

import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewResponse {
    private long reviewId;
    private String content;
    private long reviewLike;
    private double movieScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MovieReviewUserResponse user;
    private boolean likedByUser;

}
