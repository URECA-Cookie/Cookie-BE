package com.cookie.domain.review.service;

import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.entity.Review;
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
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

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

}