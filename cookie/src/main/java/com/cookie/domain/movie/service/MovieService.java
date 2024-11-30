package com.cookie.domain.movie.service;


import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
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
import com.cookie.domain.user.dto.response.GenreScoreResponse;
import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.GenreScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final GenreScoreRepository genreScoreRepository;


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


    private String title;
    private String poster;
    private String releasedAt;
    private String country;
    private Long likes;
    private Long reviews;
    @Transactional(readOnly = true)
    public List<MovieSimpleResponse> getLikedMoviesByUserId(Long userId) {
        List<MovieLike> likedMovies = movieLikeRepository.findAllByUserIdWithMovies(userId);

        return likedMovies.stream()
                .map(movieLike -> MovieSimpleResponse.builder()
                        .title(movieLike.getMovie().getTitle())
                        .poster(movieLike.getMovie().getPoster())
                        .releasedAt(movieLike.getMovie().getReleasedAt())
                        .country(movieLike.getMovie().getCountry().getName())
                        .likes(movieLike.getMovie().getMovieLikes())
                        .reviews((long) (movieLike.getMovie().getReviews() != null ? movieLike.getMovie().getReviews().size() : 0)) // 리뷰 수
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieDetails(Long movieId, Long userId) {
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
                .likes(movieRepository.countLikesByMovieId(movie.getId()))
                .certification(movie.getCertification())
                .images(movieImages.stream()
                        .map(MovieImage::getUrl)
                        .collect(Collectors.toList()))
                .videos(movie.getYoutubeUrl())
                .country(movie.getCountry().getName())
                .build();
    }


    public List<MovieSimpleResponse> getMoviesByCategoryId(Long categoryId) {
        // 카테고리 ID로 영화 리스트 조회
        List<Movie> movies = movieCategoryRepository.findMoviesByCategoryId(categoryId);

        // 조회된 영화 데이터를 MovieSimpleResponse DTO로 변환
        return movies.stream()
                .map(movie -> MovieSimpleResponse.builder()
                        .title(movie.getTitle()) // 영화 제목
                        .poster(movie.getPoster()) // 포스터 URL
                        .releasedAt(movie.getReleasedAt())
                        .country(movie.getCountry().getName())
                        .likes(movieRepository.countLikesByMovieId(movie.getId())) // Movie 엔티티에 likes 관련 정보 없음
                        .reviews((long) (movie.getReviews() != null ? movie.getReviews().size() : 0)) // 리뷰 수
                        .build())
                .collect(Collectors.toList());
    }


    public List<MovieSimpleResponse> getRecommendedMovies(Long userId) {
        // 1. 사용자 장르 점수 가져오기
        GenreScoreResponse genreScoreResponse = genreScoreRepository.findGenreScoresByUserId(userId);
        if (genreScoreResponse == null) {
            throw new IllegalArgumentException("No genre scores found for user ID: " + userId);
        }

        // 2. 상위 3개 장르 추출
        Map<String, Long> genreMap = mapGenreScores(genreScoreResponse);
        List<String> topGenres = genreMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .filter(entry -> entry.getValue() > 0) // 0점 이상인 장르만
                .limit(3) // 상위 3개 장르만
                .map(Map.Entry::getKey)
                .toList();

        // 3. 중복 방지를 위한 Set
        Set<Long> seenMovieIds = new HashSet<>();
        List<MovieSimpleResponse> recommendedMovies = new ArrayList<>();

        // 4. 각 장르별로 영화 가져오기
        for (String genre : topGenres) {
            List<MovieSimpleResponse> movies = movieRepository.findTop3MoviesByCategory(genre);

            for (MovieSimpleResponse movie : movies) {
                if (!seenMovieIds.contains(movie.getId())) {
                    recommendedMovies.add(movie);
                    seenMovieIds.add(movie.getId());
                }
                // 최대 9개로 제한
                if (recommendedMovies.size() == 9) {
                    return recommendedMovies;
                }
            }
        }

        return recommendedMovies;
    }


    private Map<String, Long> mapGenreScores(GenreScoreResponse genreScoreResponse) {
        Map<String, Long> genreMap = new HashMap<>();
        genreMap.put("로맨스", genreScoreResponse.getRomance());
        genreMap.put("공포", genreScoreResponse.getHorror());
        genreMap.put("코미디", genreScoreResponse.getComedy());
        genreMap.put("액션", genreScoreResponse.getAction());
        genreMap.put("판타지", genreScoreResponse.getFantasy());
        genreMap.put("애니메이션", genreScoreResponse.getAnimation());
        genreMap.put("범죄", genreScoreResponse.getCrime());
        genreMap.put("SF", genreScoreResponse.getSf());
        genreMap.put("음악", genreScoreResponse.getMusic());
        genreMap.put("스릴러", genreScoreResponse.getThriller());
        genreMap.put("전쟁", genreScoreResponse.getWar());
        genreMap.put("다큐멘터리", genreScoreResponse.getDocumentary());
        genreMap.put("드라마", genreScoreResponse.getDrama());
        genreMap.put("가족", genreScoreResponse.getFamily());
        genreMap.put("역사", genreScoreResponse.getHistory());
        genreMap.put("미스터리", genreScoreResponse.getMistery());
        genreMap.put("TV 영화", genreScoreResponse.getTvMovie());
        genreMap.put("서부극", genreScoreResponse.getWestern());
        genreMap.put("모험", genreScoreResponse.getAdventure());
        return genreMap;
    }



}


