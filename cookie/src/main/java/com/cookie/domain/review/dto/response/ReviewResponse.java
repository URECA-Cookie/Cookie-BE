package com.cookie.domain.review.dto.response;

import lombok.Builder;
import com.cookie.domain.movie.dto.response.ReviewMovieResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.dto.response.ReviewUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private double movieScore;
    private boolean isHide;
    private boolean isSpoiler;
    private long reviewLike;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReviewMovieResponse movie;
    private ReviewUserResponse user;

    public static ReviewResponse fromReview(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getMovieScore(),
                review.isHide(),
                review.isSpoiler(),
                review.getReviewLike(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                new ReviewMovieResponse(
                        review.getMovie().getPoster(),
                        review.getMovie().getTitle()
                ),
                new ReviewUserResponse(
                        review.getUser().getNickname(),
                        review.getUser().getProfileImage(),
                        review.getUser().getMainBadge() != null ? review.getUser().getMainBadge().getBadgeImage() : null
                )
        );
    }

}
