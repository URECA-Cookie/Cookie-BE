package com.cookie.domain.review.controller;


import com.cookie.domain.review.dto.request.ReviewCommentRequest;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.dto.response.ReviewDetailResponse;
import com.cookie.domain.review.dto.response.ReviewListResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.dto.request.UpdateReviewRequest;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    private final CopyOnWriteArrayList<SseEmitter> reviewEmitters = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters = new CopyOnWriteArrayList<>();


    // 리뷰 피드 실시간 연결
    @GetMapping("/subscribe/feed")
    public SseEmitter subscribe() {
        SseEmitter emitter = getSseEmitter(reviewEmitters);
        try {
            emitter.send(SseEmitter.event().name("connected").data("리뷰피드 실시간 연결이 성공적으로 열렸습니다."));
        } catch (Exception e) {
            log.error("리뷰피드 연결 오류", e);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    // 푸시 알림 실시간 연결
    @GetMapping("/subscribe/push-notification")
    public SseEmitter subscribePushNotification() {
        log.info("푸시알림 실시간 연결 시작");
        SseEmitter emitter = getSseEmitter(pushNotificationEmitters);
        try {
            emitter.send(SseEmitter.event().name("connected").data("푸시 알림 실시간 연결이 성공적으로 열렸습니다."));
        } catch (Exception e) {
            log.error("푸시 알림 연결 오류", e);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private SseEmitter getSseEmitter(CopyOnWriteArrayList<SseEmitter> emitters) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 연결 타임아웃 설정
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    @PostMapping("/{userId}")
    public ApiSuccess<?> createReview(@PathVariable(name = "userId") Long userId, @RequestBody CreateReviewRequest createReviewRequest) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.createReview(userId, createReviewRequest, reviewEmitters, pushNotificationEmitters);
        return ApiUtil.success("SUCCESS");
    }

    @PutMapping("/{reviewId}")
    public ApiSuccess<?> updateReview(@PathVariable(name = "reviewId") Long reviewId, @RequestBody UpdateReviewRequest updateReviewRequest) {
        reviewService.updateReview(reviewId, updateReviewRequest);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping
    public ApiSuccess<?> getReviewList(Pageable pageable) {
        Long userId = 1L;
        ReviewListResponse reviewList = reviewService.getReviewList(userId, pageable);
        return ApiUtil.success(reviewList);
    }

    @GetMapping("/spoiler")
    public ApiSuccess<?> getSpoilerReviewList(Pageable pageable) {
        Long userId = 1L;
        ReviewListResponse reviewList = reviewService.getSpoilerReviewList(userId, pageable);
        return ApiUtil.success(reviewList);
    }

    @DeleteMapping("/{reviewId}")
    public ApiSuccess<?> deleteReview(@PathVariable(name = "reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/{reviewId}")
    public ApiSuccess<?> getReviewDetail(@PathVariable(name = "reviewId") Long reviewId) {
        Long userId = 2L;
        ReviewDetailResponse reviewDetailResponse = reviewService.getReviewDetail(reviewId, userId);
        return ApiUtil.success(reviewDetailResponse);
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

    @DeleteMapping("/comments/{commentId}")
    public ApiSuccess<?> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.deleteComment(commentId);
        return ApiUtil.success("SUCCESS");
    }

}
