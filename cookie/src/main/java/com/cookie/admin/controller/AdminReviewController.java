package com.cookie.admin.controller;

import com.cookie.admin.dto.response.*;
import com.cookie.admin.service.reviewAndLike.AdminReviewAndLikeService;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReviewController {

    private final AdminReviewAndLikeService adminReviewAndLikeService;
    private final ReviewService reviewService;

    @GetMapping("/reviews/{movieId}")
    public ApiSuccess<?> getMovieReviews(@PathVariable("movieId") Long movieId,
                                         @RequestParam(value = "dateOrder", defaultValue = "latest") String dateOrder,
                                         @RequestParam(value = "likesOrder", defaultValue = "asc") String likesOrder,
                                         @RequestParam(value = "movieScoreFilter", required = false) Integer movieScoreFilter) {
        List<AdminReviewResponse> data = adminReviewAndLikeService.getMovieReviews(movieId, dateOrder, likesOrder, movieScoreFilter);
        return ApiUtil.success(data);
    }

    @GetMapping("/reviews/detail/{reviewId}")
    public ApiSuccess<?> getMovieReviewsDetail(@PathVariable("reviewId") Long reviewId) {
        AdminReviewDetailResponse data = adminReviewAndLikeService.getMovieReviewsDetail(reviewId);
        return ApiUtil.success(data);
    }

    @GetMapping("/reviews/detail/{reviewId}/comments")
    public ApiSuccess<?> getMovieReviewsDetailComments(@PathVariable("reviewId") Long reviewId) {
        List<AdminReviewDetailCommentsResponse> data = adminReviewAndLikeService.getMovieReviewsDetailComments(reviewId);
        return ApiUtil.success(data);
    }

    @DeleteMapping("/reviews/detail/{commentId}/comments")
    public ApiSuccess<?> deleteReviewComment(@PathVariable("commentId") Long commentId) {
        reviewService.deleteComment(commentId);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/reviews/detail/{reviewId}/likes")
    public ApiSuccess<?> getMovieReviewsDetailLikes(@PathVariable("reviewId") Long reviewId) {
        List<AdminReviewDetailLikesResponse> data = adminReviewAndLikeService.getMovieReviewsDetailLikes(reviewId);
        return ApiUtil.success(data);
    }

    @PostMapping("/reviews/hide/{reviewId}")
    public ApiSuccess<?> updateReviewHideStatus(@PathVariable("reviewId") Long reviewId,
                                                @RequestBody boolean hideStatus) {
        AdminReviewHideResponse data = adminReviewAndLikeService.updateReviewHideStatus(reviewId, hideStatus);
        return ApiUtil.success(data);
    }

    @PostMapping("/reviews/spoiler/{reviewId}")
    public ApiSuccess<?> updateReviewSpoilerStatus(@PathVariable("reviewId") Long reviewId,
                                                   @RequestBody boolean spoilerStatus) {
        AdminReviewSpoilerResponse data = adminReviewAndLikeService.updateReviewSpoilerStatus(reviewId, spoilerStatus);
        return ApiUtil.success(data);
    }

    @GetMapping("/likes/{movieId}")
    public ApiSuccess<?> getMovieLikes(@PathVariable("movieId") Long movieId,
                                       @RequestParam(value = "dateOrder", defaultValue = "latest") String dateOrder) {
        AdminMovieLikesResponse data = adminReviewAndLikeService.getMovieLikes(movieId, dateOrder);
        return ApiUtil.success(data);
    }
}
