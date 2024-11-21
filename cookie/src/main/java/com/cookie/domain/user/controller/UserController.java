package com.cookie.domain.user.controller;

import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.review.dto.response.ReviewCommentResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.domain.user.dto.request.MyProfileRequest;
import com.cookie.domain.user.dto.response.BadgeAccResponse;
import com.cookie.domain.user.dto.response.MyPageResponse;
import com.cookie.domain.user.dto.response.MyProfileDataResponse;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getMyPage(@PathVariable Long userId) {
        try {
            // 서비스 호출을 통해 MyPage 데이터를 가져옴
            MyPageResponse myPageDto = userService.getMyPage(userId);

            // 성공 응답 반환
            return ResponseEntity.ok(ApiUtil.success(myPageDto));
        } catch (Exception e) {
            // 예외 발생 시 오류 응답 반환
            return ResponseEntity.status(500)
                    .body(ApiUtil.error(500, e.getMessage()));
        }
    }

    @GetMapping("/{userId}/movieLiked")
    public ResponseEntity<?> getLikedMoviesByUserId(@PathVariable Long userId) {
        List<MovieResponse> likedMovies = movieLikeService.getLikedMoviesByUserId(userId);
        return ResponseEntity.ok(ApiUtil.success(likedMovies));
    }

    @GetMapping("/{userId}/reviewLiked")
    public ResponseEntity<?> getLikedReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> likedReviews = reviewService.getLikedReviewsByUserId(userId);
        return ResponseEntity.ok(ApiUtil.success(likedReviews));
    }

    @GetMapping("/{userId}/profileData")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        MyProfileDataResponse profileData = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiUtil.success(profileData));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> updateMyProfile(@PathVariable Long userId, @RequestBody MyProfileRequest request) {
        userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiUtil.success("Profile updated successfully"));
    }

    @GetMapping("/{userId}/badgePoint")
    public ResponseEntity<?> getBadgePointsByUserId(@PathVariable Long userId) {
        BadgeAccResponse badgeAccResponse = userService.getBadgeAccumulationPoint(userId);
        return ResponseEntity.ok(ApiUtil.success(badgeAccResponse));
    }

    @GetMapping("/{userId}/myReviews")
    public ResponseEntity<?> getMyReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = userService.getReviewsByUserId(userId);
        return ResponseEntity.ok(ApiUtil.success(reviews));
    }
}
