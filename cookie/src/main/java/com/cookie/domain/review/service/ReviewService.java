package com.cookie.domain.review.service;

import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getLikedReviewsByUserId(Long userId) {
        List<ReviewLike> likedReviews = reviewLikeRepository.findAllByUserIdWithReviews(userId);

        return likedReviews.stream()
                .map(reviewLike -> {
                    var review = reviewLike.getReview();
                    return ReviewResponse.builder()
                            .reviewId(review.getId())
                            .userId(review.getUser().getId())
                            .movieId(review.getMovie().getId())
                            .content(review.getContent())
                            .movieScore(review.getMovieScore())
                            .isHide(review.isHide())
                            .isSpoiler(review.isSpoiler())
                            .reviewLike(review.getReviewLike())
                            .createdAt(review.getCreatedAt().toLocalDate())
                            .updatedAt(review.getUpdatedAt().toLocalDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
