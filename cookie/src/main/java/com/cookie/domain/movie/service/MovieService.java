package com.cookie.domain.movie.service;


import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.MovieVideoResponse;
import com.cookie.domain.movie.entity.*;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieCountryRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.response.MovieReviewResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import com.cookie.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;


@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieCountryRepository movieCountryRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieLikeRepository movieLikeRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Transactional(readOnly = true)
    public ReviewOfMovieResponse getMovieReviewList(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));

        log.info("Retrieved movie: movieId = {}", movieId);

        List<Review> reviews = reviewRepository.findReviewsByMovieId(movieId);
        log.info("Retrieved {} reviews for movieId = {}", reviews.size(), movieId);

        List<MovieReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    User user = review.getUser();
                    MovieReviewUserResponse userResponse = new MovieReviewUserResponse(
                            user.getNickname(),
                            user.getProfileImage(),
                            user.getMainBadge() != null ? user.getMainBadge().getBadgeImage() : null,
                            user.getMainBadge() != null ? user.getMainBadge().getName() : null
                    );

                    return new MovieReviewResponse(
                            review.getContent(),
                            review.getReviewLike(),
                            review.getMovieScore(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            userResponse
                    );
                }).toList();

        List<String> subCategories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCountry -> movieCountry.getCategory().getSubCategory())
                .toList();

        log.info("Categories for movieId = {}: {}", movieId, subCategories);

        List<String> countries = movieCountryRepository.findByMovieIdWithCountry(movieId).stream()
                .map(movieCountry -> movieCountry.getCountry().getCountry())
                .toList();
        log.info("Countries for movieId = {}: {}", movieId, countries);

        return new ReviewOfMovieResponse(
                movie.getTitle(),
                movie.getPoster(),
                movie.getRating().name(),
                movie.getRuntime(),
                subCategories,
                countries,
                movie.getReleasedAt(),
                reviewResponses
        );
    }

    @Transactional(readOnly = true)
    public ReviewOfMovieResponse getMovieSpoilerReviewList(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));

        log.info("Retrieved movie: movieId = {}", movieId);

        List<Review> reviews = reviewRepository.findSpoilerReviewsByMovieId(movieId);
        log.info("Retrieved {} reviews for movieId = {}", reviews.size(), movieId);

        List<MovieReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    User user = review.getUser();
                    MovieReviewUserResponse userResponse = new MovieReviewUserResponse(
                            user.getNickname(),
                            user.getProfileImage(),
                            user.getMainBadge() != null ? user.getMainBadge().getBadgeImage() : null,
                            user.getMainBadge() != null ? user.getMainBadge().getName() : null
                    );

                    return new MovieReviewResponse(
                            review.getContent(),
                            review.getReviewLike(),
                            review.getMovieScore(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            userResponse
                    );
                }).toList();

        List<String> subCategories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCountry -> movieCountry.getCategory().getSubCategory())
                .toList();

        log.info("Categories for movieId = {}: {}", movieId, subCategories);

        List<String> countries = movieCountryRepository.findByMovieIdWithCountry(movieId).stream()
                .map(movieCountry -> movieCountry.getCountry().getCountry())
                .toList();
        log.info("Countries for movieId = {}: {}", movieId, countries);

        return new ReviewOfMovieResponse(
                movie.getTitle(),
                movie.getPoster(),
                movie.getRating().name(),
                movie.getRuntime(),
                subCategories,
                countries,
                movie.getReleasedAt(),
                reviewResponses
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
                        .company(movieLike.getMovie().getCompany())
                        .releasedAt(movieLike.getMovie().getReleasedAt() != null
                                ? movieLike.getMovie().getReleasedAt().format(DATE_FORMATTER)
                                : null) // LocalDateTime -> String 변환
                        .runtime(movieLike.getMovie().getRuntime() + " minutes") // int -> String 변환
                        .score(movieLike.getMovie().getScore())
                        .rating(movieLike.getMovie().getRating().name()) // Enum -> String 변환
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

        // 3. 영화 비디오 가져오기
        List<MovieVideo> movieVideos = movieRepository.findByIdWithVideos(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie Videos not found"))
                .getMovieVideos();

        // 4. 영화 국가 가져오기
        List<MovieCountry> movieCountries = movieRepository.findByIdWithCountries(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie Countries not found"))
                .getMovieCountries();

        // 5. MovieResponse 생성
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .poster(movie.getPoster())
                .plot(movie.getPlot())
                .company(movie.getCompany())
                .releasedAt(movie.getReleasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .runtime(movie.getRuntime() + " minutes")
                .score(movie.getScore())
                .rating(movie.getRating().name())
                .images(movieImages.stream()
                        .map(MovieImage::getUrl)
                        .collect(Collectors.toList()))
                .videos(movieVideos.stream()
                        .map(video -> MovieVideoResponse.builder()
                                .url(video.getUrl())
                                .title(video.getTitle())
                                .build())
                        .collect(Collectors.toList()))
                .countries(movieCountries.stream()
                        .map(mc -> mc.getCountry().getCountry())
                        .collect(Collectors.toList()))
                .build();
    }
}
