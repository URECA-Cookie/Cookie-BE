package com.cookie.domain.movie.service;


import com.cookie.admin.dto.response.RecommendResponse;
import com.cookie.admin.entity.AdminMovieRecommend;
import com.cookie.admin.service.recommend.AdminRecommendService;
import com.cookie.domain.actor.dto.response.ActorResponse;
import com.cookie.domain.actor.repository.ActorRepository;
import com.cookie.domain.category.dto.CategoryResponse;
import com.cookie.domain.category.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.category.request.CategoryRequest;
import com.cookie.domain.director.dto.response.DirectorResponse;
import com.cookie.domain.director.service.DirectorService;
import com.cookie.domain.matchup.dto.response.MainMatchUpsResponse;
import com.cookie.domain.matchup.service.MatchUpService;
import com.cookie.domain.movie.dto.response.*;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieImage;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.response.MovieReviewResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.GenreScoreResponse;
import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import com.cookie.domain.user.dto.response.ReviewUserResponse;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.GenreScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
    private final CategoryRepository categoryRepository;
    private final MatchUpService matchUpService;
    private final DirectorService directorService;
    private final ActorRepository actorRepository;
    private final AdminRecommendService adminRecommendService;


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
    public MoviePagenationResponse getLikedMoviesByUserId(Long userId, int page, int size) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // MovieLikeRepository를 통해 좋아요 누른 영화 조회
        Page<MovieLike> likedMoviesPage = movieLikeRepository.findAllByUserIdWithMovies(userId, pageable);

        // MovieLike -> MovieSimpleResponse 변환
        List<MovieSimpleResponse> movies = likedMoviesPage.getContent().stream()
                .map(movieLike -> MovieSimpleResponse.builder()
                        .id(movieLike.getMovie().getId())
                        .title(movieLike.getMovie().getTitle())
                        .poster(movieLike.getMovie().getPoster())
                        .releasedAt(movieLike.getMovie().getReleasedAt())
                        .country(movieLike.getMovie().getCountry().getName())
                        .likes(movieLike.getMovie().getMovieLikes())
                        .reviews((long) (movieLike.getMovie().getReviews() != null ? movieLike.getMovie().getReviews().size() : 0))
                        .build())
                .collect(Collectors.toList());

        // MoviePagenationResponse 반환
        return MoviePagenationResponse.builder()
                .currentPage(page)
                .movies(movies)
                .totalPages(likedMoviesPage.getTotalPages())
                .build();
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

        // 3. 감독 정보 가져오기
        DirectorResponse directorResponse = DirectorResponse.builder()
                .id(movie.getDirector().getId())
                .name(movie.getDirector().getName())
                .profileImage(movie.getDirector().getProfileImage())
                .build();

        List<ActorResponse> actors = actorRepository.findActorsByMovieId(movieId);

        // 4. 리뷰 가져오기
        List<ReviewResponse> reviews = reviewRepository.findReviewsByMovieId(movieId).stream()
                .limit(4) // 최대 4개의 리뷰만 가져옴
                .map(review -> {
                    // 리뷰 정보를 ReviewResponse로 변환<<<<<<< feature/#57-RefactorLikeAuth
                    return ReviewResponse.fromReview(review, reviewRepository.existsById(userId),Long.valueOf(review.getReviewComments().size()));

                })
                .collect(Collectors.toList());

        // 5. 카테고리 리스트
        List<CategoryResponse> categories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCategory -> new CategoryResponse(
                        movieCategory.getCategory().getId(),
                        movieCategory.getCategory().getMainCategory(),
                        movieCategory.getCategory().getSubCategory()
                ))
                .collect(Collectors.toList());

        // 6. 사용자가 해당 영화에 좋아요를 눌렀는지 안 눌렀는지 여부
        boolean isLiked = movieLikeRepository.isMovieLikedByUser(movieId,userId);

        // 7. MovieResponse 생성
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
                .video(movie.getYoutubeUrl())
                .country(movie.getCountry().getName())
                .director(directorResponse)
                .actors(actors)
                .reviews(reviews) // 리뷰 리스트 추가
                .categories(categories)
                .isLiked(isLiked)
                .build();
    }



    @Cacheable(value = "categoryMoviesCache" , cacheManager = "categoryMoviesCache")
    public MoviePagenationResponse getMoviesByCategory(String mainCategory, String subCategory, int page, int size) {
        // 1. mainCategory와 subCategory로 Category ID 조회
        Category category = categoryRepository.findByMainCategoryAndSubCategory(
                        mainCategory, subCategory)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // 2. 카테고리 ID로 영화 리스트 조회 (페이지네이션 적용)
        Pageable pageable = PageRequest.of(page, size); // 페이지는 0부터 시작하므로 page-1
        Page<Movie> moviePage = movieCategoryRepository.findMoviesByCategoryIdWithPagination(category.getId(), pageable);

        // 3. 조회된 영화 데이터를 MovieSimpleResponse DTO로 변환
        List<MovieSimpleResponse> movies = moviePage.getContent().stream()
                .map(movie -> MovieSimpleResponse.builder()
                        .id(movie.getId())
                        .title(movie.getTitle()) // 영화 제목
                        .poster(movie.getPoster()) // 포스터 URL
                        .releasedAt(movie.getReleasedAt()) // 출시일
                        .country(movie.getCountry().getName()) // 국가
                        .likes(movieRepository.countLikesByMovieId(movie.getId())) // 좋아요 수
                        .reviews((long) (movie.getReviews() != null ? movie.getReviews().size() : 0)) // 리뷰 수
                        .build())
                .collect(Collectors.toList());

        // 4. 페이지네이션 정보를 포함한 응답 생성
        return MoviePagenationResponse.builder()
                .currentPage(page)
                .movies(movies)
                .totalPages(moviePage.getTotalPages())
                .build();
    }



    public List<MovieSimpleResponse> getRecommendedMovies(Long userId) {
        // 1. 사용자 장르 점수 가져오기
        GenreScoreResponse genreScoreResponse = genreScoreRepository.findGenreScoresByUserId(userId);
        if (genreScoreResponse == null) {
            throw new IllegalArgumentException("No genre scores found for user ID: " + userId);
        }

        // 2. 장르 점수 맵핑
        Map<String, Long> genreMap = mapGenreScores(genreScoreResponse);
        List<Map.Entry<String, Long>> sortedGenres = genreMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // 0점 이상인 장르만
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey())) // 동일 점수일 때 알파벳 순 정렬
                .toList();

        // 예외 처리: 장르 점수가 없는 경우
        if (sortedGenres.isEmpty()) {
            throw new IllegalArgumentException("No genres with scores greater than 0 for user ID: " + userId);
        }

        // 3. 중복 방지를 위한 Set
        Set<Long> seenMovieIds = new HashSet<>();
        List<MovieSimpleResponse> recommendedMovies = new ArrayList<>();

        // 4. 장르 개수에 따라 로직 처리
        if (sortedGenres.size() == 1) {
            // 하나의 장르만 있는 경우 해당 장르에서 9개 추천
            String genre = sortedGenres.get(0).getKey();
            recommendedMovies.addAll(fetchMoviesForGenre(genre, 9, seenMovieIds));
        } else if (sortedGenres.size() == 2) {
            // 두 개의 장르가 있는 경우 높은 점수에서 5개, 두 번째에서 4개 추천
            String firstGenre = sortedGenres.get(0).getKey();
            String secondGenre = sortedGenres.get(1).getKey();
            recommendedMovies.addAll(fetchMoviesForGenre(firstGenre, 5, seenMovieIds));
            recommendedMovies.addAll(fetchMoviesForGenre(secondGenre, 4, seenMovieIds));
        } else {
            // 세 개 이상의 장르가 있는 경우 상위 3개 장르에서 각각 3개 추천
            for (int i = 0; i < 3; i++) {
                String genre = sortedGenres.get(i).getKey();
                recommendedMovies.addAll(fetchMoviesForGenre(genre, 3, seenMovieIds));
            }
        }

        // 결과가 9개를 넘을 경우 잘라서 반환
        return recommendedMovies.size() > 9 ? recommendedMovies.subList(0, 9) : recommendedMovies;
    }

    private List<MovieSimpleResponse> fetchMoviesForGenre(String genre, int limit, Set<Long> seenMovieIds) {
        List<MovieSimpleResponse> movies = movieRepository.findTopMoviesByCategory(genre);
        List<MovieSimpleResponse> filteredMovies = new ArrayList<>();

        for (MovieSimpleResponse movie : movies) {
            if (!seenMovieIds.contains(movie.getId())) {
                filteredMovies.add(movie);
                seenMovieIds.add(movie.getId());
            }
            if (filteredMovies.size() == limit) {
                break;
            }
        }

        return filteredMovies;
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


   @Cacheable(value = "mainAdminRecommendCache", cacheManager = "mainAdminRecommendCacheManager") // Caffeine Cache 적용
   public List<MovieSimpleResponse> getMainAdminRecommend(){
       List<RecommendResponse> recommendMovies = adminRecommendService.getRecommendMovies();
       List<MovieSimpleResponse> movieSimpleResponses = recommendMovies.stream()
            .map(recommendResponse -> {
                // movieId로 Movie 조회
                Movie movie = movieRepository.findById(recommendResponse.getMovieId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Movie not found with id: " + recommendResponse.getMovieId()));

                // Movie 데이터를 기반으로 MovieSimpleResponse 생성
                return MovieSimpleResponse.builder()
                        .id(movie.getId()) // Movie ID
                        .title(movie.getTitle()) // 제목
                        .poster(movie.getPoster()) // 포스터
                        .releasedAt(movie.getReleasedAt()) // 개봉일
                        .country(movie.getCountry().getName()) // 제작 국가
                        .likes(movieRepository.countLikesByMovieId(movie.getId())) // 좋아요 수
                        .reviews((long) movie.getReviews().size()) // 리뷰 수
                        .build();
            })
            .toList();


        return movieSimpleResponses;

   }




}


