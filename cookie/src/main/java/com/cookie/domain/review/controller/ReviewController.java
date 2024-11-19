package com.cookie.domain.review.controller;

import com.cookie.domain.review.dto.CreateReviewDto;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{userId}")
    public ApiSuccess<?> createReview(@PathVariable(name = "userId") Long userId, @RequestBody CreateReviewDto createReviewDto) {
        // TODO: userId JWT 토큰으로 변경
        reviewService.createReview(userId, createReviewDto);
        return ApiUtil.success("SUCCESS");
    }
}
