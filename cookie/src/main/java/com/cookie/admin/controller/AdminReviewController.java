package com.cookie.admin.controller;

import com.cookie.admin.dto.response.*;
import com.cookie.admin.service.reviewAndLike.AdminReviewAndLikeService;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin 영화 리뷰 및 좋아요 관리", description = "Admin 영화 리뷰 및 좋아요 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReviewController {

    private final AdminReviewAndLikeService adminReviewAndLikeService;
    private final ReviewService reviewService;

    @Operation(summary = "영화 리뷰 전체 리스트 With Query", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = AdminReviewResponse.class))))
    })
    @GetMapping("/reviews/{movieId}")
    public ApiSuccess<?> getMovieReviews(@PathVariable("movieId") Long movieId,
                                         @RequestParam(value = "dateOrder", defaultValue = "latest") String dateOrder,
                                         @RequestParam(value = "likesOrder", defaultValue = "asc") String likesOrder,
                                         @RequestParam(value = "movieScoreFilter", required = false) Integer movieScoreFilter) {
        List<AdminReviewResponse> data = adminReviewAndLikeService.getMovieReviews(movieId, dateOrder, likesOrder, movieScoreFilter);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 리뷰 상세 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminReviewDetailResponse.class)))
    })
    @GetMapping("/reviews/detail/{reviewId}")
    public ApiSuccess<?> getMovieReviewsDetail(@PathVariable("reviewId") Long reviewId) {
        AdminReviewDetailResponse data = adminReviewAndLikeService.getMovieReviewsDetail(reviewId);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 리뷰 삭제", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @DeleteMapping("/reviews/detail/{reviewId}")
    public ApiSuccess<?> deleteReviewAdmin(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "영화 리뷰 댓글 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = AdminReviewDetailCommentsResponse.class))))
    })
    @GetMapping("/reviews/detail/{reviewId}/comments")
    public ApiSuccess<?> getMovieReviewsDetailComments(@PathVariable("reviewId") Long reviewId) {
        List<AdminReviewDetailCommentsResponse> data = adminReviewAndLikeService.getMovieReviewsDetailComments(reviewId);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 리뷰 댓글 삭제", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @DeleteMapping("/reviews/detail/{commentId}/comments")
    public ApiSuccess<?> deleteReviewComment(@PathVariable("commentId") Long commentId) {
        reviewService.deleteComment(commentId);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "영화 리뷰 좋아요 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = AdminReviewDetailLikesResponse.class))))
    })
    @GetMapping("/reviews/detail/{reviewId}/likes")
    public ApiSuccess<?> getMovieReviewsDetailLikes(@PathVariable("reviewId") Long reviewId) {
        List<AdminReviewDetailLikesResponse> data = adminReviewAndLikeService.getMovieReviewsDetailLikes(reviewId);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 리뷰 숨기기", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminReviewHideResponse.class)))
    })
    @PostMapping("/reviews/hide/{reviewId}")
    public ApiSuccess<?> updateReviewHideStatus(@PathVariable("reviewId") Long reviewId,
                                                @RequestBody boolean hideStatus) {
        AdminReviewHideResponse data = adminReviewAndLikeService.updateReviewHideStatus(reviewId, hideStatus);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 리뷰 스포일러 리뷰로 변경", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminReviewSpoilerResponse.class)))
    })
    @PostMapping("/reviews/spoiler/{reviewId}")
    public ApiSuccess<?> updateReviewSpoilerStatus(@PathVariable("reviewId") Long reviewId,
                                                   @RequestBody boolean spoilerStatus) {
        AdminReviewSpoilerResponse data = adminReviewAndLikeService.updateReviewSpoilerStatus(reviewId, spoilerStatus);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 좋아요 리스트 With Query", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminMovieLikesResponse.class)))
    })
    @GetMapping("/likes/{movieId}")
    public ApiSuccess<?> getMovieLikes(@PathVariable("movieId") Long movieId,
                                       @RequestParam(value = "dateOrder", defaultValue = "latest") String dateOrder) {
        AdminMovieLikesResponse data = adminReviewAndLikeService.getMovieLikes(movieId, dateOrder);
        return ApiUtil.success(data);
    }
}
