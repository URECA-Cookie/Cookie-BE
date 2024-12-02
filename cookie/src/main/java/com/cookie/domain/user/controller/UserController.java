package com.cookie.domain.user.controller;

import com.cookie.domain.movie.dto.response.MoviePagenationResponse;
import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.review.dto.response.ReviewPagenationResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.domain.user.dto.response.BadgeAccResponse;
import com.cookie.domain.user.dto.response.MyPageResponse;
import com.cookie.domain.user.dto.response.MyProfileDataResponse;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReviewService reviewService;


    @GetMapping()
    public ApiSuccess<?> getMyPage(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        log.info("userId: {}", userId);
        MyPageResponse myPageDto = userService.getMyPage(userId);
        return ApiUtil.success(myPageDto);

    }

    @GetMapping("/likedMovieList")
    public ApiSuccess<?> getLikedMoviesByUserId(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam(name="page", defaultValue = "0") int page, // 요청 페이지 번호 (기본값: 0)
            @RequestParam(name="size", defaultValue = "10") int size // 페이지 크기 (기본값 10)
    ) {
        Long userId = customOAuth2User.getId();
        MoviePagenationResponse response = movieService.getLikedMoviesByUserId(userId, page, size);
        return ApiUtil.success(response);
    }

    @GetMapping("/likedReviewList")
    public ApiSuccess<?> getLikedReviewsByUserId(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam(name="page", defaultValue = "0") int page, // 요청 페이지 번호 (기본값: 0)
            @RequestParam(name="size", defaultValue = "10") int size // 페이지 크기 (기본값: 10)
    ) {
        Long userId = customOAuth2User.getId();
        ReviewPagenationResponse response = reviewService.getLikedReviewsByUserId(userId, page, size);
        return ApiUtil.success(response);
    }

    @GetMapping("/profileData")
    public ApiSuccess<?> getUserProfile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        MyProfileDataResponse profileData = userService.getMyProfile(userId);
        return ApiUtil.success(profileData);
    }

    @PostMapping
    public ApiSuccess<?> updateMyProfile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                         @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                         @RequestPart(value = "nickname") String nickname,
                                         @RequestPart(value = "mainBadgeId", required = false) String mainBadgeId,
                                         @RequestPart(value = "genreId") String genreIdStr) {
        Long userId = customOAuth2User.getId();
        userService.updateMyProfile(userId, profileImage, nickname, mainBadgeId, genreIdStr);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("/badgePoint")
    public ApiSuccess<?> getBadgePointsByUserId(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        BadgeAccResponse badgeAccResponse = userService.getBadgeAccumulationPoint(userId);
        return ApiUtil.success(badgeAccResponse);
    }

    @GetMapping("/myReviews")
    public ApiSuccess<?> getMyReviewsByUserId(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        List<ReviewResponse> reviews = userService.getReviewsByUserId(userId);
        return ApiUtil.success(reviews);
    }

    @PostMapping("/movie-like/{movieId}")
    public ApiSuccess<?> toggleMovieLike(@PathVariable(name="movieId") Long movieId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        userService.toggleMovieLike(movieId, userId);
        return ApiUtil.success("SUCCESS");
    }

    @PostMapping("/review-like/{reviewId}")
    public ApiSuccess<?> toggleReviewLike(@PathVariable(name="reviewId") Long reviewId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        userService.toggleReviewLike(reviewId, userId);
        return ApiUtil.success("SUCCESS");

    }
}
