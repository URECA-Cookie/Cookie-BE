package com.cookie.domain.user.controller;

import com.cookie.domain.movie.dto.response.MoviePagenationResponse;
import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.review.dto.response.ReviewPagenationResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.domain.user.dto.request.MyProfileRequest;
import com.cookie.domain.user.dto.response.BadgeAccResponse;
import com.cookie.domain.user.dto.response.MyPageResponse;
import com.cookie.domain.user.dto.response.MyProfileDataResponse;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReviewService reviewService;


    @GetMapping("/{userId}")
    public ApiSuccess<?> getMyPage(@PathVariable(name="userId") Long userId) {
        // 서비스 호출을 통해 MyPage 데이터를 가져옴
        MyPageResponse myPageDto = userService.getMyPage(userId);
        return ApiUtil.success(myPageDto);

    }

    @GetMapping("/{userId}/likedMovieList")
    public ApiSuccess<?> getLikedMoviesByUserId(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name="page", defaultValue = "0") int page, // 요청 페이지 번호 (기본값: 0)
            @RequestParam(name="size", defaultValue = "10") int size // 페이지 크기 (기본값 10)
    ) {
        MoviePagenationResponse response = movieService.getLikedMoviesByUserId(userId, page, size);
        return ApiUtil.success(response);
    }

    @GetMapping("/{userId}/likedReviewList")
    public ApiSuccess<?> getLikedReviewsByUserId(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name="page", defaultValue = "0") int page, // 요청 페이지 번호 (기본값: 0)
            @RequestParam(name="size", defaultValue = "10") int size // 페이지 크기 (기본값: 10)
    ) {
        ReviewPagenationResponse response = reviewService.getLikedReviewsByUserId(userId, page, size);
        return ApiUtil.success(response);
    }

    @GetMapping("/{userId}/profileData")
    public ApiSuccess<?> getUserProfile(@PathVariable(name="userId") Long userId) {
        MyProfileDataResponse profileData = userService.getMyProfile(userId);
        return ApiUtil.success(profileData);
    }

    @PostMapping("/{userId}")
    public ApiSuccess<?> updateMyProfile(@PathVariable(name="userId") Long userId, @RequestBody MyProfileRequest request) {
        userService.updateMyProfile(userId, request);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/{userId}/badgePoint")
    public ApiSuccess<?> getBadgePointsByUserId(@PathVariable(name="userId") Long userId) {
        BadgeAccResponse badgeAccResponse = userService.getBadgeAccumulationPoint(userId);
        return ApiUtil.success(badgeAccResponse);
    }

    @GetMapping("/{userId}/myReviews")
    public ApiSuccess<?> getMyReviewsByUserId(@PathVariable(name="userId") Long userId) {
        List<ReviewResponse> reviews = userService.getReviewsByUserId(userId);
        return ApiUtil.success(reviews);
    }

    @PostMapping("/{movieId}/movieLike")
    public ApiSuccess<?> toggleMovieLike(@PathVariable(name="movieId") Long movieId, @RequestParam(name="userId") Long userId) {
        userService.toggleMovieLike(movieId, userId);
        return ApiUtil.success("SUCCESS");
    }

    @PostMapping("/{reviewId}/reviewLike")
    public ApiUtil.ApiSuccess<?> toggleReviewLike(@PathVariable(name="reviewId") Long reviewId, @RequestParam(name="userId") Long userId) {
        reviewService.toggleReviewLike(reviewId, userId);
        return ApiUtil.success("SUCCESS");

    }
}
