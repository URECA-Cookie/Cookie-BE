package com.cookie.domain.movie.controller;



import com.cookie.domain.matchup.dto.response.MainMatchUpsResponse;
import com.cookie.domain.matchup.service.MatchUpService;
import com.cookie.domain.movie.dto.response.*;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import com.google.protobuf.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "영화", description = "영화 API")
@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MatchUpService matchUpService;

    @Operation(summary = "영화 상세 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MovieResponse.class)))
    })

    @GetMapping("/{movieId}")
    public ApiSuccess<MovieResponse> getMovieDetail(
            @PathVariable(name="movieId") Long movieId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        MovieResponse movieDetail = movieService.getMovieDetails(movieId, userId);
        return ApiUtil.success(movieDetail);
    }

    @Operation(summary = "영화에 작성 된 리뷰", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewOfMovieResponse.class)))
    })
    @GetMapping("{movieId}/reviews")
    public ApiSuccess<?> getMovieReviewList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMovieReviewList(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);
    }

    @Operation(summary = "영화에 작성 된 스포일러 리뷰", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewOfMovieResponse.class)))
    })
    @GetMapping("{movieId}/reviews/spoiler")
    public ApiSuccess<?> getMovieSpoilerReviewList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMovieSpoilerReviewList(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);

    }

    @Operation(summary = "영화에 작성 된 리뷰 좋아요순", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewOfMovieResponse.class)))
    })
    @GetMapping("{movieId}/reviews/most-liked")
    public ApiSuccess<?> getMostLikedMovieReviews(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMostLikedMovieReviews(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);
    }

    @Operation(summary = "영화에 작성 된 스포일러 리뷰 좋아요순", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewOfMovieResponse.class)))
    })
    @GetMapping("{movieId}/reviews/spoiler/most-liked")
    public ApiSuccess<?> getMostLikedMovieSpoilerReviews(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMostLikedMovieSpoilerReviews(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);
    }

    @Operation(summary = "카테고리로 영화 리스트 조회", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MoviePagenationResponse.class)))
    })
    @GetMapping("/categoryMovies")
    public ResponseEntity<MoviePagenationResponse> getMoviesByCategoryId(
            @RequestParam(name="mainCategory") String mainCategory,
            @RequestParam(name="subCategory") String subCategory,
            @RequestParam(name="page", defaultValue = "0") int page, // 요청 페이지 번호 (기본값: 0)
            @RequestParam(name="size", defaultValue = "10") int size) // 페이지 크기 (기본값: 10))
    {
        MoviePagenationResponse movies = movieService.getMoviesByCategory(mainCategory, subCategory, page, size);
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "유저 개인화 추천 영화 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = MovieSimpleResponse.class))))
    })
    @GetMapping("/recommendations")
    public ApiSuccess<List<MovieSimpleResponse>> getRecommendations(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        List<MovieSimpleResponse> recommendedMovies = movieService.getRecommendedMovies(userId);
        return ApiUtil.success(recommendedMovies);
    }

    @Operation(summary = "메인 페이지 (매치 업 영화)", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MainMatchUpsResponse.class)))
    })
    @GetMapping("/mainMatchUps")
    public ApiSuccess<MainMatchUpsResponse> getMainPageMatchUps(){
        MainMatchUpsResponse mainMatchUpsResponse = matchUpService.getMainMatchUps();
        return ApiUtil.success(mainMatchUpsResponse);
    }

    @Operation(summary = "관리자 추천 영화 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = MovieSimpleResponse.class))))
    })
    @GetMapping("/mainAdminRecommend")
    public ApiSuccess<List<MovieSimpleResponse>> getMainAdminRecommend(){
        List<MovieSimpleResponse> mainAdminRecommend = movieService.getMainAdminRecommend();
        return ApiUtil.success(mainAdminRecommend);
    }


}
