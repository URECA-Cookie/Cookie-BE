package com.cookie.domain.user.controller;

import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.service.MovieService;
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
    private final MovieService movieLikeService;
    private final ReviewService reviewService;

    @GetMapping("/{userId}")
    public ApiSuccess<?> getMyPage(@PathVariable Long userId) {
        // 서비스 호출을 통해 MyPage 데이터를 가져옴
        MyPageResponse myPageDto = userService.getMyPage(userId);
        return ApiUtil.success(myPageDto);

    }

    @GetMapping("/{userId}/movieLiked")
    public ApiSuccess<?> getLikedMoviesByUserId(@PathVariable Long userId) {
        List<MovieResponse> likedMovies = movieLikeService.getLikedMoviesByUserId(userId);
        return ApiUtil.success(likedMovies);
    }

//    @GetMapping("/{userId}/reviewLiked")
//    public ApiSuccess<?> getLikedReviewsByUserId(@PathVariable Long userId) {
//        List<ReviewResponse> likedReviews = reviewService.getLikedReviewsByUserId(userId);
//        return ApiUtil.success(likedReviews);
//    }

    @GetMapping("/{userId}/profileData")
    public ApiSuccess<?> getUserProfile(@PathVariable Long userId) {
        MyProfileDataResponse profileData = userService.getMyProfile(userId);
        return ApiUtil.success(profileData);
    }

    @PostMapping("/{userId}")
    public ApiSuccess<?> updateMyProfile(@PathVariable Long userId, @RequestBody MyProfileRequest request) {
        userService.updateMyProfile(userId, request);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/{userId}/badgePoint")
    public ApiSuccess<?> getBadgePointsByUserId(@PathVariable Long userId) {
        BadgeAccResponse badgeAccResponse = userService.getBadgeAccumulationPoint(userId);
        return ApiUtil.success(badgeAccResponse);
    }

    @GetMapping("/{userId}/myReviews")
    public ApiSuccess<?> getMyReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = userService.getReviewsByUserId(userId);
        return ApiUtil.success(reviews);
    }
}
