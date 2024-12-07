package com.cookie.domain.user.controller;

import com.cookie.domain.movie.dto.response.MoviePagenationResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.review.dto.response.ReviewPagenationResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.service.ReviewService;
import com.cookie.domain.user.dto.response.BadgeAccResponse;
import com.cookie.domain.user.dto.response.MyPageResponse;
import com.cookie.domain.user.dto.response.MyProfileDataResponse;
import com.cookie.domain.user.dto.response.UserResponse;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "유저", description = "유저")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReviewService reviewService;

    @Operation(summary = "내 정보 페이지", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MyPageResponse.class)))
    })
    @GetMapping("")
    public ApiSuccess<?> getMyPage(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        log.info("userId: {}", userId);
        MyPageResponse myPageDto = userService.getMyPage(userId);
        return ApiUtil.success(myPageDto);

    }

    @Operation(summary = "좋아요 누른 영화 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MoviePagenationResponse.class)))
    })
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

    @Operation(summary = "좋아요 누른 리뷰 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewPagenationResponse.class)))
    })
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

    @Operation(summary = "내 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MyProfileDataResponse.class)))
    })
    @GetMapping("/profileData")
    public ApiSuccess<?> getUserProfile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        MyProfileDataResponse profileData = userService.getMyProfile(userId);
        return ApiUtil.success(profileData);
    }

    @Operation(summary = "내 정보 수정", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
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

    @Operation(summary = "내 뱃지 포인트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BadgeAccResponse.class)))
    })
    @GetMapping("/badgePoint")
    public ApiSuccess<?> getBadgePointsByUserId(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        BadgeAccResponse badgeAccResponse = userService.getBadgeAccumulationPoint(userId);
        return ApiUtil.success(badgeAccResponse);
    }

    @Operation(summary = "내가 작성한 리뷰 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ReviewResponse.class))))
    })
    @GetMapping("/myReviews")
    public ApiSuccess<?> getMyReviewsByUserId(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam int page,
            @RequestParam int size) {
        Long userId = customOAuth2User.getId();
        ReviewPagenationResponse response = userService.getReviewsPagenation(userId, page, size);
        return ApiUtil.success(response);
    }


    @Operation(summary = "영화 좋아요 생성", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping("/movie-like/{movieId}")
    public ApiSuccess<?> toggleMovieLike(@PathVariable(name="movieId") Long movieId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        userService.toggleMovieLike(movieId, userId);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "리뷰 좋아요 생성", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping("/review-like/{reviewId}")
    public ApiSuccess<?> toggleReviewLike(@PathVariable(name="reviewId") Long reviewId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        userService.toggleReviewLike(reviewId, userId);
        return ApiUtil.success("SUCCESS");

    }

    @Operation(summary = "info", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BadgeAccResponse.class)))
    })
    @GetMapping("/info")
    public ApiSuccess<?> getUserInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        UserResponse userResponse = userService.getUserInfo(userId);
        return ApiUtil.success(userResponse);

    }

    @Operation(summary = "계정 삭제", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @DeleteMapping
    public ApiSuccess<?> deleteUserAccount(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        userService.deleteUserAccount(userId);
        return ApiUtil.success("SUCCESS");
    }
}
