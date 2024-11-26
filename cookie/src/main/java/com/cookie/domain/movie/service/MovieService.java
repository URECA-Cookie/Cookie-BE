package com.cookie.domain.movie.service;


import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieImage;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.response.MovieReviewResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import com.cookie.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;


    @Transactional(readOnly = true)
    public ReviewOfMovieResponse getMovieReviewList(Long movieId, Long userId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));

        log.info("Retrieved movie: movieId = {}", movieId);

        Page<Review> reviewsPage = reviewRepository.findReviewsByMovieId(movieId, pageable);
        log.info("Retrieved {} reviews for movieId = {}", reviewsPage.getContent().size(), movieId);

        List<MovieReviewResponse> reviewResponses = reviewsPage.stream()
                .map(review -> {
                    User user = review.getUser();
                    MovieReviewUserResponse userResponse = new MovieReviewUserResponse(
                            user.getNickname(),
                            user.getProfileImage(),
                            user.getMainBadge() != null ? user.getMainBadge().getBadgeImage() : null,
                            user.getMainBadge() != null ? user.getMainBadge().getName() : null
                    );

                    boolean likedByUser = userId != null && reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), userId);

                    return new MovieReviewResponse(
                            review.getId(),
                            review.getContent(),
                            review.getReviewLike(),
                            review.getMovieScore(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            userResponse,
                            likedByUser
                    );
                }).toList();

        List<String> subCategories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCountry -> movieCountry.getCategory().getSubCategory())
                .toList();

        log.info("Categories for movieId = {}: {}", movieId, subCategories);

        log.info("Countries for movieId = {}: {}", movieId, movie.getCountry().getName());

        return new ReviewOfMovieResponse(
                movie.getTitle(),
                movie.getPoster(),
                movie.getCertification(),
                movie.getRuntime(),
                subCategories,
                movie.getCountry().getName(),
                movie.getReleasedAt(),
                reviewResponses,
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ReviewOfMovieResponse getMovieSpoilerReviewList(Long movieId, Long userId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));

        log.info("Retrieved movie: movieId = {}", movieId);

        Page<Review> reviewsPage = reviewRepository.findSpoilerReviewsByMovieId(movieId, pageable);
        log.info("Retrieved {} reviews for movieId = {}", reviewsPage.getContent().size(), movieId);

        List<MovieReviewResponse> reviewResponses = reviewsPage.stream()
                .map(review -> {
                    User user = review.getUser();
                    MovieReviewUserResponse userResponse = new MovieReviewUserResponse(
                            user.getNickname(),
                            user.getProfileImage(),
                            user.getMainBadge() != null ? user.getMainBadge().getBadgeImage() : null,
                            user.getMainBadge() != null ? user.getMainBadge().getName() : null
                    );

                    boolean likedByUser = userId != null && reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), userId);

                    return new MovieReviewResponse(
                            review.getId(),
                            review.getContent(),
                            review.getReviewLike(),
                            review.getMovieScore(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            userResponse,
                            likedByUser
                    );
                }).toList();

        List<String> subCategories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCountry -> movieCountry.getCategory().getSubCategory())
                .toList();

        log.info("Categories for movieId = {}: {}", movieId, subCategories);

        return new ReviewOfMovieResponse(
                movie.getTitle(),
                movie.getPoster(),
                movie.getCertification(),
                movie.getRuntime(),
                subCategories,
                movie.getCountry().getName(),
                movie.getReleasedAt(),
                reviewResponses,
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getLikedMoviesByUserId(Long userId) {
        List<MovieLike> likedMovies = movieLikeRepository.findAllByUserIdWithMovies(userId);

        return likedMovies.stream()
                .map(movieLike -> MovieResponse.builder()
                        .id(movieLike.getMovie().getId())
                        .title(movieLike.getMovie().getTitle())
                        .poster(movieLike.getMovie().getPoster())
                        .plot(movieLike.getMovie().getPlot())
                        .releasedAt(movieLike.getMovie().getReleasedAt())
                        .runtime(movieLike.getMovie().getRuntime())
                        .score(movieLike.getMovie().getScore())
                        .certification(movieLike.getMovie().getCertification()) // Enum -> String 변환
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieDetails(Long movieId) {
        // 1. 기본 영화 정보 가져오기
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movieId));

        // 2. 영화 이미지 가져오기
        List<MovieImage> movieImages = movieRepository.findByIdWithImages(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie Images not found"))
                .getMovieImages();

        // 5. MovieResponse 생성
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .poster(movie.getPoster())
                .plot(movie.getPlot())
                .releasedAt(movie.getReleasedAt())
                .runtime(movie.getRuntime())
                .score(movie.getScore())
                .certification(movie.getCertification())
                .images(movieImages.stream()
                        .map(MovieImage::getUrl)
                        .collect(Collectors.toList()))
                .videos(movie.getYoutubeUrl())
                .country(movie.getCountry().getName())
                .build();
    }
}
