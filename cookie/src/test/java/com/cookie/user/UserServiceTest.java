package com.cookie.user;

import com.cookie.domain.badge.dto.MyBadgeResponse;
import com.cookie.domain.badge.entity.Badge;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.review.dto.response.ReviewDetailResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.request.MyProfileRequest;
import com.cookie.domain.user.dto.response.*;
import com.cookie.domain.user.entity.BadgeAccumulationPoint;
import com.cookie.domain.user.entity.GenreScore;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.UserBadge;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.domain.user.repository.BadgeAccumulationPointRepository;
import com.cookie.domain.user.repository.GenreScoreRepository;
import com.cookie.domain.user.repository.UserBadgeRepository;
import com.cookie.domain.user.repository.UserRepository;
import com.cookie.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private GenreScoreRepository genreScoreRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private BadgeAccumulationPointRepository badgeAccumulationPointRepository;

    @Test
    void testGetMyPage() {
        // Given
        Long userId = 1L;

        // 사용자 생성
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .socialProvider(SocialProvider.GOOGLE)
                .email("johndoe@gmail.com")
                .role(Role.USER)
                .socialId("12345")
                .build();

        // Reflection을 사용하여 id 설정
        ReflectionTestUtils.setField(user, "id", userId);

        // 리뷰 생성
        Review review = Review.builder()
                .movie(new Movie("Inception", "inception.jpg", "Great movie", "Warner Bros", null, 120, 9.0, null))
                .user(user)
                .content("Amazing movie!")
                .movieScore(9.0)
                .isHide(false)
                .isSpoiler(false)
                .reviewLike(100)
                .build();

        // Reflection을 사용하여 id와 createdAt 설정
        ReflectionTestUtils.setField(review, "id", 1L);
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "updatedAt", LocalDateTime.now());

        // Mock 데이터 생성
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(reviewRepository.findAllByUserIdWithMovie(userId)).thenReturn(List.of(review));

        // When
        MyPageResponse response = userService.getMyPage(userId);

        // Then
        assertNotNull(response);
        assertEquals("JohnDoe", response.getNickname());
        assertEquals("profile.jpg", response.getProfileImage());
        assertEquals(1, response.getReviews().size());
        assertEquals("Amazing movie!", response.getReviews().get(0).getContent());
    }


    @Test
    void testUpdateMyProfile() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("old_profile.jpg")
                .build();

        // Reflection을 사용해 id 설정
        ReflectionTestUtils.setField(user, "id", userId);

        List<UserBadge> userBadges = List.of(
                new UserBadge(true, user, new Badge("Action Maniac", "action_badge.jpg", 200)),
                new UserBadge(false, user, new Badge("Romance Lover", "romance_badge.jpg", 100))
        );

        MyProfileRequest request = new MyProfileRequest("new_profile.jpg", "JaneDoe", "Romance Lover");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userBadgeRepository.findAllByUserId(userId)).thenReturn(userBadges);

        // When
        userService.updateMyProfile(userId, request);

        // Then
        assertEquals("JaneDoe", user.getNickname());
        assertEquals("new_profile.jpg", user.getProfileImage());
        assertFalse(userBadges.get(0).isMain());
        assertTrue(userBadges.get(1).isMain());
    }


    @Test
    void testGetAllBadgesByUserId() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .build();

        // Reflection을 사용해 id 설정
        ReflectionTestUtils.setField(user, "id", userId);

        List<UserBadge> userBadges = List.of(
                new UserBadge(true, user, new Badge("Romance Lover", "romance_badge.jpg", 100)),
                new UserBadge(false, user, new Badge("Action Maniac", "action_badge.jpg", 200))
        );

        Mockito.when(userBadgeRepository.findAllByUserId(userId)).thenReturn(userBadges);

        // When
        List<MyBadgeResponse> badgeResponses = userService.getAllBadgesByUserId(userId);

        // Then
        assertNotNull(badgeResponses);
        assertEquals(2, badgeResponses.size());
        assertEquals("Romance Lover", badgeResponses.get(0).getName());
        assertEquals("romance_badge.jpg", badgeResponses.get(0).getBadgeImage());
    }


    @Test
    void testGetGenreScoresByUserId() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .build();

        // Reflection을 사용해 id 설정
        ReflectionTestUtils.setField(user, "id", userId);

        List<GenreScore> genreScores = List.of(
                new GenreScore(user, 10L, 8L, 7L, 5L, 6L, 3L, 2L, 9L, 0L, 4L, 0L, 1L, 0L)
        );

        Mockito.when(genreScoreRepository.findAllByUserId(userId)).thenReturn(genreScores);

        // When
        List<GenreScoreResponse> genreScoreResponses = userService.getGenreScoresByUserId(userId);

        // Then
        assertNotNull(genreScoreResponses);
        assertEquals(1, genreScoreResponses.size());
        assertEquals(10L, genreScoreResponses.get(0).getRomance());
        assertEquals(8L, genreScoreResponses.get(0).getHorror());
    }

    @Test
    void testGetMyProfile() {
        // Given
        Long userId = 1L;

        // Mock 데이터 생성
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Badge badge1 = new Badge("Action Maniac", "action_badge.jpg", 200);
        ReflectionTestUtils.setField(badge1, "id", 1L);

        Badge badge2 = new Badge("Romance Lover", "romance_badge.jpg", 100);
        ReflectionTestUtils.setField(badge2, "id", 2L);

        List<UserBadge> userBadges = List.of(
                new UserBadge(true, user, badge1),
                new UserBadge(false, user, badge2)
        );

        // Mocking 리포지토리 호출
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userBadgeRepository.findAllByUserId(userId)).thenReturn(userBadges);

        // When
        MyProfileDataResponse response = userService.getMyProfile(userId);

        // Then
        assertNotNull(response);
        assertEquals("JohnDoe", response.getNickname());
        assertEquals("profile.jpg", response.getProfileImage());
        assertEquals(2, response.getBadges().size());
        assertEquals("Action Maniac", response.getBadges().get(0).getName());
        assertTrue(response.getBadges().get(0).isMain());
    }

    @Test
    void testGetBadgeAccumulationPoint() {
        // Given
        Long userId = 1L;

        // Mock 데이터 생성
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        BadgeAccumulationPoint badgeAccumulationPoint = BadgeAccumulationPoint.builder()
                .user(user)
                .romancePoint(10L)
                .horrorPoint(5L)
                .comedyPoint(7L)
                .actionPoint(12L)
                .fantasyPoint(6L)
                .animationPoint(3L)
                .crimePoint(8L)
                .sfPoint(4L)
                .musicPoint(1L)
                .thrillerPoint(2L)
                .queerPoint(0L)
                .warPoint(0L)
                .documentaryPoint(9L)
                .build();

        // Mocking 리포지토리 호출
        Mockito.when(badgeAccumulationPointRepository.findByUserId(userId)).thenReturn(badgeAccumulationPoint);

        // When
        BadgeAccResponse response = userService.getBadgeAccumulationPoint(userId);

        // Then
        assertNotNull(response);
        assertEquals(10L, response.getRomancePoint());
        assertEquals(5L, response.getHorrorPoint());
        assertEquals(7L, response.getComedyPoint());
        assertEquals(12L, response.getActionPoint());
        assertEquals(9L, response.getDocumentaryPoint());
    }

    @Test
    void testGetReviewsByUserId() {
        // Given
        Long userId = 1L;

        // Mock Badge 데이터 생성
        Badge badge = Badge.builder()
                .name("Super Reviewer")
                .badgeImage("badge.jpg")
                .needPoint(1000)
                .build();

        // Mock User 데이터 생성
        User user = User.builder()
                .nickname("JohnDoe")
                .profileImage("profile.jpg")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        // Mock UserBadge 데이터 생성
        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .isMain(true)
                .build();
        user.getUserBadges().add(userBadge);

        // Mock Movie 데이터 생성
        Movie movie = new Movie("Inception", "inception.jpg", "Great movie", "Warner Bros", null, 120, 9.0, null);
        ReflectionTestUtils.setField(movie, "id", 1L);

        // Mock Review 데이터 생성
        Review review = Review.builder()
                .movie(movie)
                .user(user)
                .content("Amazing movie!")
                .movieScore(9.0)
                .isHide(false)
                .isSpoiler(false)
                .reviewLike(100)
                .build();
        ReflectionTestUtils.setField(review, "id", 1L);
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(review, "updatedAt", LocalDateTime.now());

        // Mocking 리포지토리 호출
        Mockito.when(reviewRepository.findAllByUserIdWithMovie(userId)).thenReturn(List.of(review));

        // When
        List<ReviewResponse> responses = userService.getReviewsByUserId(userId);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        ReviewResponse response = responses.get(0);

        // Assert Review details
        assertEquals(1L, response.getReviewId());
        assertEquals("Amazing movie!", response.getContent());
        assertEquals(9.0, response.getMovieScore(), 0.01);
        assertFalse(response.isHide());
        assertFalse(response.isSpoiler());
        assertEquals(100, response.getReviewLike());

        // Assert Movie details
        assertNotNull(response.getMovie());
        assertEquals("Inception", response.getMovie().getTitle());
        assertEquals("inception.jpg", response.getMovie().getPoster());

        // Assert User details
        assertNotNull(response.getUser());
        assertEquals("JohnDoe", response.getUser().getNickname());
        assertEquals("profile.jpg", response.getUser().getProfileImage());
        assertEquals("badge.jpg", response.getUser().getMainBadgeImage());

        // Assert Timestamps
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(review.getCreatedAt(), response.getCreatedAt());
        assertEquals(review.getUpdatedAt(), response.getUpdatedAt());
    }







}