//package com.cookie.movie;
//
//import com.cookie.domain.country.entity.Country;
//import com.cookie.domain.movie.dto.response.MovieResponse;
//import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
//import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
//import com.cookie.domain.movie.entity.*;
//import com.cookie.domain.movie.entity.enums.Rating;
//import com.cookie.domain.movie.repository.MovieCategoryRepository;
//import com.cookie.domain.movie.repository.MovieCountryRepository;
//import com.cookie.domain.movie.repository.MovieLikeRepository;
//import com.cookie.domain.movie.repository.MovieRepository;
//import com.cookie.domain.movie.service.MovieService;
//import com.cookie.domain.review.entity.Review;
//import com.cookie.domain.review.repository.ReviewRepository;
//import com.cookie.domain.user.entity.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@ExtendWith(MockitoExtension.class)
//class MovieServiceTest {
//
//    @Mock
//    private MovieRepository movieRepository;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private MovieCountryRepository movieCountryRepository;
//
//    @Mock
//    private MovieCategoryRepository movieCategoryRepository;
//
//    @Mock
//    private MovieLikeRepository movieLikeRepository;
//
//    @InjectMocks
//    private MovieService movieService;
//
////    @Test
////    void testGetMovieDetails() {
////        // Given
////        Long movieId = 1L;
////        Movie movie = Movie.builder()
////                .title("Inception")
////                .poster("inception.jpg")
////                .plot("A mind-bending thriller.")
////                .company("Warner Bros")
////                .releasedAt(LocalDateTime.of(2010, 7, 16, 0, 0))
////                .runtime(148)
////                .score(8.8)
////                .rating(Rating.CHILD)
////                .build();
////        ReflectionTestUtils.setField(movie, "id", movieId);
////
////        // Mock 데이터를 설정
////        List<MovieImage> movieImages = List.of(new MovieImage("image1.jpg", movie));
////        List<MovieVideo> movieVideos = List.of(new MovieVideo("trailer1.mp4", "Official Trailer", movie));
////        List<MovieCountry> movieCountries = List.of(
////                new MovieCountry(movie, Country.builder().country("USA").build()),
////                new MovieCountry(movie, Country.builder().country("UK").build())
////        );
////        Long likes = 120L;
////
////        // Movie 객체의 연관 리스트 설정
////        ReflectionTestUtils.setField(movie, "movieImages", movieImages);
////        ReflectionTestUtils.setField(movie, "movieVideos", movieVideos);
////        ReflectionTestUtils.setField(movie, "movieCountries", movieCountries);
////
////        // Mock Repository 호출 결과
////        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
////        Mockito.when(movieRepository.findByIdWithImages(movieId)).thenReturn(Optional.of(movie));
////        Mockito.when(movieRepository.findByIdWithVideos(movieId)).thenReturn(Optional.of(movie));
////        Mockito.when(movieRepository.findByIdWithCountries(movieId)).thenReturn(Optional.of(movie));
////        Mockito.when(movieLikeRepository.countByMovieId(movieId)).thenReturn(likes);
////
////        // When
////        MovieResponse response = movieService.getMovieDetails(movieId);
////
////        // Then
////        assertNotNull(response);
////        assertEquals("Inception", response.getTitle());
////        assertEquals("inception.jpg", response.getPoster());
////        assertEquals(likes, response.getLikes());
////
////        // 추가 검증
////        assertNotNull(response.getImages());
////        assertEquals(1, response.getImages().size());
////        assertEquals("image1.jpg", response.getImages().get(0));
////    }
//
//    @Test
//    void testGetMovieReviewList() {
//        // Given
//        Long movieId = 1L;
//        Movie movie = Movie.builder()
//                .title("Inception")
//                .poster("inception.jpg")
//                .rating(Rating.CHILD)
//                .runtime(148)
//                .releasedAt(LocalDateTime.of(2010, 7, 16, 0, 0))
//                .build();
//        ReflectionTestUtils.setField(movie, "id", movieId);
//
//        User user = User.builder().nickname("JohnDoe").profileImage("profile.jpg").build();
//        Review review = Review.builder()
//                .movie(movie)
//                .user(user)
//                .content("Great movie!")
//                .movieScore(8.5)
//                .reviewLike(100)
//                .build();
//        List<Review> reviews = List.of(review);
//
//        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
//        Mockito.when(reviewRepository.findReviewsByMovieId(movieId)).thenReturn(reviews);
//
//        // When
//        ReviewOfMovieResponse response = movieService.getMovieReviewList(movieId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("Inception", response.getTitle());
//        assertEquals("15세", response.getCertification());
//        assertNotNull(response.getReviews());
//        assertEquals(1, response.getReviews().size());
//    }
//
//    @Test
//    void testGetMovieSpoilerReviewList() {
//        // Given
//        Long movieId = 1L;
//        Movie movie = Movie.builder()
//                .title("Inception")
//                .poster("inception.jpg")
//                .certification("15세")
//                .runtime(148)
//                .releasedAt("LocalDateTime.of(2010, 7, 16, 0, 0)")
//                .build();
//        ReflectionTestUtils.setField(movie, "id", movieId);
//
//        User user = User.builder().nickname("JohnDoe").profileImage("profile.jpg").build();
//        Review review = Review.builder()
//                .movie(movie)
//                .user(user)
//                .content("Spoiler alert!")
//                .movieScore(8.5)
//                .reviewLike(80)
//                .build();
//        List<Review> reviews = List.of(review);
//
//        Mockito.when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
//        Mockito.when(reviewRepository.findSpoilerReviewsByMovieId(movieId)).thenReturn(reviews);
//
//        // When
//        ReviewOfMovieResponse response = movieService.getMovieSpoilerReviewList(movieId);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("Inception", response.getTitle());
//        assertNotNull(response.getReviews());
//        assertEquals(1, response.getReviews().size());
//    }
//
//    @Test
//    void testGetLikedMoviesByUserId() {
//        // Given
//        Long userId = 1L;
//        Movie movie = Movie.builder()
//                .title("Inception")
//                .poster("inception.jpg")
//                .plot("A mind-bending thriller.")
//                .company("Warner Bros")
//                .releasedAt(LocalDateTime.of(2010, 7, 16, 0, 0))
//                .runtime(148)
//                .score(8.8)
//                .rating(Rating.CHILD)
//                .build();
//        ReflectionTestUtils.setField(movie, "id", 1L);
//
//        MovieLike movieLike = new MovieLike(User.builder().build(), movie);
//        Mockito.when(movieLikeRepository.findAllByUserIdWithMovies(userId)).thenReturn(List.of(movieLike));
//
//        // When
//        List<MovieSimpleResponse> responses = movieService.getLikedMoviesByUserId(userId);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Inception", responses.get(0).getTitle());
//    }
//
//    @Test
//    void testGetMoviesByCategoryId() {
//        // Given
//        Long categoryId = 1L;
//        Movie movie = Movie.builder()
//                .title("Inception")
//                .poster("inception.jpg")
//                .plot("A mind-bending thriller.")
//                .company("Warner Bros")
//                .releasedAt(LocalDateTime.of(2010, 7, 16, 0, 0))
//                .runtime(148)
//                .score(8.8)
//                .rating(Rating.ADULT)
//                .build();
//        ReflectionTestUtils.setField(movie, "id", 1L);
//
//        Mockito.when(movieCategoryRepository.findMoviesByCategoryId(categoryId)).thenReturn(List.of(movie));
//
//        // When
//        List<MovieSimpleResponse> responses = movieService.getMoviesByCategoryId(categoryId);
//
//        // Then
//        assertNotNull(responses);
//        assertEquals(1, responses.size());
//        assertEquals("Inception", responses.get(0).getTitle());
//    }
//}
