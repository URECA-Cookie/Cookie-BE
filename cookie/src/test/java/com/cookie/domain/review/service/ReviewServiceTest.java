package com.cookie.domain.review.service;

import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.dto.request.UpdateReviewRequest;
import com.cookie.domain.review.dto.response.ReviewListResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Movie movie;
    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@email.com")
                .nickname("nickname")
                .build();

        movie = Movie.builder()
                .title("movie title")
                .build();
    }

    @Test
    @DisplayName("영화 리뷰 등록 성공 테스트")
    void createReview_success() {
        Long userId = 1L;
        Long movieId = 1L;

        CreateReviewRequest createReviewRequest = new CreateReviewRequest(
                movieId, "재밌었어요!", 4, false
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(reviewRepository.findByUserAndMovie(user, movie)).willReturn(Optional.empty());

        Review savedReview = createReviewRequest.toEntity(user, movie);
        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        CopyOnWriteArrayList<SseEmitter> reviewEmitters = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters = new CopyOnWriteArrayList<>();

        reviewService.createReview(userId, createReviewRequest, reviewEmitters, pushNotificationEmitters);

        assertThat(savedReview.getContent()).isEqualTo(createReviewRequest.getContent());
        assertThat(savedReview.getMovieScore()).isEqualTo(createReviewRequest.getMovieScore());
        assertThat(savedReview.getMovie().getTitle()).isEqualTo(movie.getTitle());

    }


    @Test
    @DisplayName("영화 리뷰 등록 실패 테스트 - MovieNotFound")
    void createReview_fail_MovieNotFound() {
        Long userId = 1L;
        Long movieId = 999L;

        CreateReviewRequest createReviewRequest = new CreateReviewRequest(
                movieId, "재밌었어요!", 4, false
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.empty());

        CopyOnWriteArrayList<SseEmitter> reviewEmitters = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters = new CopyOnWriteArrayList<>();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(userId, createReviewRequest, reviewEmitters, pushNotificationEmitters);
        });

        assertThat(exception.getMessage()).contains("not found movieId");
    }


    @Test
    @DisplayName("영화 리뷰 등록 실패 테스트 - 이미 리뷰를 등록한 영화")
    void createReview_Fail_AlreadyExists() {
        Long userId = 1L;
        Long movieId = 1L;

        CreateReviewRequest createReviewRequest = new CreateReviewRequest(
                movieId, "재밌었어요!", 4, false
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));

        given(reviewRepository.findByUserAndMovie(user, movie)).willReturn(Optional.of(mock(Review.class)));

        CopyOnWriteArrayList<SseEmitter> reviewEmitters = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters = new CopyOnWriteArrayList<>();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(userId, createReviewRequest, reviewEmitters, pushNotificationEmitters);
        });

        assertThat(exception.getMessage()).contains("해당 영화에 이미 리뷰를 등록했습니다.");
    }

    @Test
    @DisplayName("영화 리뷰 수정 성공 테스트")
    void updateReview_Success() {
        Long reviewId = 1L;
        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest("updated 리뷰", 5, true);

        Review review = Review.builder()
                .content("리뷰")
                .movieScore(3)
                .isSpoiler(false)
                .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        reviewService.updateReview(reviewId, updateReviewRequest);

        assertThat(review.getContent()).isEqualTo(updateReviewRequest.getContent());
        assertThat(review.getMovieScore()).isEqualTo(updateReviewRequest.getMovieScore());
        assertThat(review.isSpoiler()).isEqualTo(updateReviewRequest.getIsSpoiler());

    }

    @Test
    @DisplayName("영화 리뷰 수정 실패 테스트 - NotFoundReview")
    void updateReview_Fail_NotFound() {
        Long reviewId = 1L;

        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest(
                "Updated 리뷰", 5, true
        );

        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, updateReviewRequest);
        });

        assertThat(exception.getMessage()).contains("not found reviewId");
    }

    @Test
    @DisplayName("영화 리뷰 삭제 성공 테스트")
    void deleteReview_Success() {
        Long reviewId = 1L;

        Review review = Review.builder()
                .content("review")
                .build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(reviewRepository).delete(review);
    }


    @Test
    @DisplayName("영화 리뷰 삭제 실패 테스트 - NotFoundReview")
    void deleteReview_Fail_NotFound() {
        Long reviewId = 999L;

        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(reviewId);
        });

        assertThat(exception.getMessage()).contains("not found reviewId");
    }

    @Test
    @DisplayName("영화 리뷰 피드 조회 성공 테스트")
    void getReviewList_Success() {
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        Review review = Review.builder()
                .movie(movie)
                .user(user)
                .content("review")
                .movieScore(3)
                .build();

        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);

        given(reviewRepository.findAllWithMovieAndUser(pageable)).willReturn(reviewPage);

        ReviewListResponse result = reviewService.getReviewList(userId, pageable);

        assertThat(result.getReviews().size()).isEqualTo(1);
        assertThat(result.getTotalReviews()).isEqualTo(1);
        assertThat(result.getTotalReviewPages()).isEqualTo(1);
        assertThat(result.getReviews().get(0).isLikedByUser()).isFalse();

    }

    @Test
    @DisplayName("영화 스포일러 리뷰 피드 조회 성공 테스트")
    void getSpoilerReviewList_Success() {
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        Review review = Review.builder()
                .movie(movie)
                .user(user)
                .content("review")
                .movieScore(3)
                .isSpoiler(true)
                .build();

        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);

        given(reviewRepository.findAllWithMovieAndUserWithSpoilers(pageable)).willReturn(reviewPage);

        ReviewListResponse result = reviewService.getSpoilerReviewList(userId, pageable);

        assertThat(result.getReviews().size()).isEqualTo(1);
        assertThat(result.getTotalReviews()).isEqualTo(1);
        assertThat(result.getTotalReviewPages()).isEqualTo(1);
        assertThat(result.getReviews().get(0).isLikedByUser()).isFalse();

    }


}