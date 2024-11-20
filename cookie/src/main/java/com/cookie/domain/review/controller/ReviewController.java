package com.cookie.domain.review.controller;

import com.cookie.domain.review.dto.request.ReviewCommentRequest;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.dto.response.ReviewDetailResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.dto.request.UpdateReviewRequest;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{userId}")
    public ApiSuccess<?> createReview(@PathVariable(name = "userId") Long userId, @RequestBody CreateReviewRequest createReviewRequest) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.createReview(userId, createReviewRequest);
        return ApiUtil.success("SUCCESS");
    }

    @PutMapping("/{reviewId}")
    public ApiSuccess<?> updateReview(@PathVariable(name = "reviewId") Long reviewId, @RequestBody UpdateReviewRequest updateReviewRequest) {
        reviewService.updateReview(reviewId, updateReviewRequest);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping
    public ApiSuccess<?> getReviewList() {
        List<ReviewResponse> reviewList = reviewService.getReviewList();
        return ApiUtil.success(reviewList);
    }

    @DeleteMapping("/{reviewId}")
    public ApiSuccess<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/{reviewId}")
    public ApiSuccess<?> getReviewDetail(@PathVariable(name = "reviewId") Long reviewId) {
        ReviewDetailResponse reviewDetailResponse = reviewService.getReviewDetail(reviewId);
        return ApiUtil.success(reviewDetailResponse);
    }

    @PostMapping("/{reviewId}/like/{userId}")
    public ApiSuccess<?> addReviewLike(@PathVariable(name = "reviewId") Long reviewId, @PathVariable(name = "userId") Long userId) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.addReviewLike(reviewId, userId);
        return ApiUtil.success("SUCCESS");
    }

    @PostMapping("/{reviewId}/comments/{userId}")
    public ApiSuccess<?> createComment(@PathVariable(name = "reviewId") Long reviewId, @PathVariable(name = "userId") Long userId, @RequestBody ReviewCommentRequest reviewCommentRequest) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.createComment(reviewId, userId, reviewCommentRequest);
        return ApiUtil.success("SUCCESS");
    }

    @PutMapping("/comments/{commentId}")
    public ApiSuccess<?> updateComment(@PathVariable(name = "commentId") Long commentId, @RequestBody ReviewCommentRequest reviewCommentRequest) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.updateComment(commentId, reviewCommentRequest);
        return ApiUtil.success("SUCCESS");
    }



}
