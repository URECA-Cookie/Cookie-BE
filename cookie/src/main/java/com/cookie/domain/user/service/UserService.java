package com.cookie.domain.user.service;

import com.cookie.domain.category.repository.CategoryRepository;
import com.cookie.domain.badge.dto.MyBadgeResponse;
import com.cookie.domain.badge.repository.BadgeRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.repository.MatchUpRepository;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.notification.dto.request.FcmTokenRequest;
import com.cookie.domain.notification.repository.FcmTokenRepository;
import com.cookie.domain.notification.service.FcmTokenService;
import com.cookie.domain.notification.service.NotificationService;
import com.cookie.domain.review.dto.response.ReviewPagenationResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.reward.entity.RewardHistory;
import com.cookie.domain.reward.repository.RewardHistoryRepository;
import com.cookie.domain.user.dto.response.*;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.*;
import com.cookie.domain.user.entity.enums.ActionType;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.domain.user.repository.*;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.global.service.AWSS3Service;
import com.cookie.domain.notification.entity.FcmToken;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserBadgeRepository userBadgeRepository;
    private final GenreScoreService genreScoreService;
    private final GenreScoreRepository genreScoreRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BadgeAccumulationPointRepository badgeAccumulationPointRepository;
    private final MovieRepository movieRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final DailyGenreScoreService dailyGenreScoreService;
    private final AWSS3Service awss3Service;
    private final BadgeRepository badgeRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final NotificationService notificationService;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmTokenService fcmTokenService;
    private final RewardHistoryRepository rewardHistoryRepository;
    private final MatchUpRepository matchUpRepository;


    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(Long userId) {
        // 1. 유저의 닉네임 및 프로필 이미지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        String nickname = user.getNickname();
        String profileImage = user.getProfileImage();

        // 2. 유저의 뱃지 조회
        List<MyBadgeResponse> badgeDtos = getAllBadgesByUserId(userId);

        // 3. 유저의 뱃지 포인트 조회
        Long myBadgeTotalPoint = rewardHistoryRepository.findTotalBadgePointsByUser(userId);

        // 4. 유저의 리뷰 조회 (4개)
        List<ReviewResponse> reviewDtos = getReviewsByUserId(userId).stream()
                .limit(4) // 최대 4개의 리뷰만 선택
                .collect(Collectors.toList());


        // 5. MyPageResponse 생성 및 반환
        return MyPageResponse.builder()
                .nickname(nickname)
                .profileImage(profileImage)
                .pushEnabled(user.isPushEnabled())
                .badge(badgeDtos)
                .badgePoint(myBadgeTotalPoint)
                .reviews(reviewDtos)
                .build();
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findAllByUserIdWithMovie(userId);

        return reviews.stream()
                .map(review -> ReviewResponse.fromReview(review, false, Long.valueOf(review.getReviewComments().size())))
                .toList();
    }
    /**
     * 유저가 보유한 뱃지 조회
     */
    @Transactional(readOnly = true)
    public List<MyBadgeResponse> getAllBadgesByUserId(Long userId) {
        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);

        return userBadges.stream()
                .map(userBadge -> MyBadgeResponse.builder()
                        .name(userBadge.getBadge().getName())
                        .badgeImage(userBadge.getBadge().getBadgeImage())
                        .isMain(userBadge.isMain())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 유저의 장르 점수 조회
     */
    @Transactional(readOnly = true)
    public List<GenreScoreResponse> getGenreScoresByUserId(Long userId) {
        List<GenreScore> genreScores = genreScoreRepository.findAllByUserId(userId);

        return genreScores.stream()
                .map(genreScore -> GenreScoreResponse.builder()
                        .romance(genreScore.getRomance())
                        .horror(genreScore.getHorror())
                        .comedy(genreScore.getComedy())
                        .action(genreScore.getAction())
                        .sf(genreScore.getSf())
                        .fantasy(genreScore.getFantasy())
                        .animation(genreScore.getAnimation())
                        .crime(genreScore.getCrime())
                        .music(genreScore.getMusic())
                        .thriller(genreScore.getThriller())
                        .war(genreScore.getWar())
                        .documentary(genreScore.getDocumentary())
                        .drama(genreScore.getDrama())
                        .family(genreScore.getFamily())
                        .history(genreScore.getHistory())
                        .mistery(genreScore.getMistery())
                        .tvMovie(genreScore.getTvMovie())
                        .western(genreScore.getWestern())
                        .adventure(genreScore.getAdventure())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 유저의 리뷰 조회
     */

    @Transactional(readOnly = true)
    public ReviewPagenationResponse getReviewsPagenation(Long userId, int page, int size) {
        // 페이징 요청 생성
        Pageable pageable = PageRequest.of(page, size);

        // ReviewLike 엔티티를 페이징 처리하여 조회
        Page<Review> reviewsPage = reviewRepository.findAllByUserId(userId, pageable);

        // ReviewLike -> ReviewResponse 변환
        List<ReviewResponse> reviews = reviewsPage.getContent().stream()
                .map(review -> {
                    return ReviewResponse.fromReview(review, true, Long.valueOf(review.getReviewComments().size()));
                })
                .toList();

        // ReviewPagenationResponse 생성
        return ReviewPagenationResponse.builder()
                .currentPage(page)
                .reviews(reviews)
                .totalPages(reviewsPage.getTotalPages())
                .build();
    }


    public MyProfileDataResponse getMyProfile(Long userId) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 유저가 보유한 모든 뱃지 조회
        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);

        // 3. 유저의 뱃지 리스트를 DTO로 변환
        List<MyBadgeResponse> badgeResponses = userBadges.stream()
                .map(userBadge -> MyBadgeResponse.builder()
                        .name(userBadge.getBadge().getName())
                        .badgeImage(userBadge.getBadge().getBadgeImage())
                        .isMain(userBadge.isMain())
                        .build())
                .collect(Collectors.toList());

        // 4. MyProfileDataResponse 생성 및 반환
        return MyProfileDataResponse.builder()
                .profileImage(user.getProfileImage())
                .badges(badgeResponses)
                .nickname(user.getNickname())
                .genreId(user.getCategory().getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MyBadgeHistoryResponse> getMyBadgePointHistory(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        List<RewardHistory> rewardHistoryRepositories = rewardHistoryRepository.findBadgePointHistories(userId, startDate, endDate);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return rewardHistoryRepositories.stream()
                .map(history ->
                        MyBadgeHistoryResponse.builder()
                                .actionName(history.getAction())
                                .point(history.getActionPoint())
                                .movieName(history.getMovieName())
                                .createdAt(history.getCreatedAt().format(dateTimeFormatter))
                                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMyProfile(Long userId, MultipartFile profileImage, String nickname, String mainBadgeIdStr, String genreIdStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found user: " + userId));

        Long mainBadgeId = null;
        if (mainBadgeIdStr != null && !mainBadgeIdStr.isEmpty()) { // 메인 뱃지가 없을 경우 (유저가 획득한 뱃지가 존재하지 않을 경우)
            try {
                mainBadgeId = Long.parseLong(mainBadgeIdStr);
                badgeRepository.findById(mainBadgeId)
                        .orElseThrow(() -> new IllegalArgumentException("not found badge: " + mainBadgeIdStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid badge ID: " + mainBadgeIdStr);
            }
        }

        String profileImageUrl = user.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) { // profile null 이 아닐 경우
            profileImageUrl = awss3Service.uploadImage(profileImage);
        } else if (profileImageUrl == null || profileImageUrl.isEmpty()) {
            profileImageUrl = "https://uplus-bucket.s3.ap-northeast-2.amazonaws.com/6bc46d8d-b_default.jpeg";
        }

        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);
        log.info("user badge list: {}", userBadges == null ? 0 : userBadges.size());

        if (userBadges != null && !userBadges.isEmpty()) {
            Long finalMainBadgeId = mainBadgeId;
            userBadges.forEach(userBadge -> {
                if (userBadge.getBadge().getId().equals(finalMainBadgeId)) {
                    userBadge.updateMainBadge(true); // mainBadge로 설정
                } else if (userBadge.isMain()) {
                    userBadge.updateMainBadge(false); // 기존 mainBadge 해제
                }
            });
        }
        log.info("Updated user main badge: {}", user.getMainBadge() != null ? user.getMainBadge().getName() : "No main badge");

        // 기존 장르
        Long prevGenreId = user.getCategory().getId();

        // 장르 선택
        Long genreId = Long.parseLong(genreIdStr);
        Category genre = categoryRepository.findById(genreId)
                .orElseThrow(() -> new IllegalArgumentException("not found genre: " + genreId));

        user.updateProfile(profileImageUrl, nickname.trim(), genre);
        userRepository.save(user);

        if (userBadges != null && !userBadges.isEmpty()) {
            userBadgeRepository.saveAll(userBadges);
        }

        // 선호 장르 변경시, 기존 토픽 구독 취소 및 업데이트
//        if (!prevGenreId.equals(genre.getId())) {
//            if (fcmToken != null && !fcmToken.isEmpty()) {
//                notificationService.unsubscribeFromTopic(fcmToken, prevGenreId, userId);
//                log.info("Unsubscribed from previous genre: {}", prevGenreId);
//            }
//            notificationService.subscribeToTopic(fcmToken, genreId, userId);
//            log.info("Subscribed to new genre: {}", genreId);
//        } else {
//            log.warn("FCM token is null or empty. Skipping subscription update.");
//        }

        log.info("Updated user info");
    }

    public BadgeAccResponse getBadgeAccumulationPoint(Long userId) {
        // BadgeAccumulationPoint 데이터를 가져옴
        BadgeAccumulationPoint badgeAccumulationPoint = badgeAccumulationPointRepository.findByUserId(userId);

        // BadgeAccResponse로 변환하여 반환
        return BadgeAccResponse.builder()
                .accPoint(badgeAccumulationPoint.getAccPoint())
                .build();
    }


    @Transactional
    public UserResponse registerUser(User user) {
        userRepository.save(user);
        genreScoreService.createAndSaveGenreScore(user);
        initBadgeAccumulationPoint(user);

//        List<String> fcmTokens = user.getFcmTokens()
//                .stream()
//                .map(FcmToken::getToken)
//                .toList();

        return new UserResponse(user.getId(), user.getNickname(), user.getProfileImage(), user.getCategory().getId(), null);
    }

    public void registerAdmin(User user) {
        userRepository.save(user);
    }

    public boolean isDuplicateSocial(SocialProvider socialProvider, String socialId) {
        return userRepository.existsBySocialProviderAndSocialId(socialProvider, socialId);
    }

    public boolean isDuplicateNickname(String nickname, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));

        return !user.getNickname().equals(nickname) && userRepository.existsByNickname(nickname); // 존재하면
    }

    public boolean isDuplicateNicknameRegister(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isDuplicateNicknameSetting(String nickname, String userNickname) {
        return !userNickname.equals(nickname) && userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void toggleMovieLike(Long movieId, Long userId) {
        // 사용자가 존재하는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 영화가 존재하는지 확인
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));

        // 영화의 카테고리 중 메인 카테고리가 "장르"인 항목만 필터링
        List<String> genres = movie.getMovieCategories().stream()
                .filter(mc -> "장르".equals(mc.getCategory().getMainCategory())) // "장르" 필터
                .map(mc -> mc.getCategory().getSubCategory()) // SubCategory 추출
                .toList();

        // 좋아요 기록이 있는지 확인
        Optional<MovieLike> existingLike = movieLikeRepository.findByMovieAndUser(movie, user);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 삭제
            movieLikeRepository.delete(existingLike.get());
            movie.decreaseLikeCount();

            // DailyGenreScore에서 -6점 추가
            genres.forEach(genre -> dailyGenreScoreService.saveScore(user, genre, -6, ActionType.MOVIE_LIKE));


        } else {
            // 좋아요를 누르지 않았다면 새로 추가
            MovieLike movieLike = MovieLike.builder()
                    .user(user)
                    .movie(movie)
                    .build();
            movieLikeRepository.save(movieLike);
            movie.increaseLikeCount();

            // DailyGenreScore에 6점 추가
            genres.forEach(genre -> dailyGenreScoreService.saveScore(user, genre, 6, ActionType.MOVIE_LIKE));

        }
    }

    @Transactional
    public void toggleReviewLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        if (review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        ReviewLike existingLike = reviewLikeRepository.findByUserAndReview(user, review);
        if (existingLike != null) {
            reviewLikeRepository.delete(existingLike);
            review.decreaseLikeCount();
            log.info("Removed like from reviewId: {}", reviewId);
        } else {
            ReviewLike like = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(like);
            review.increaseLikeCount();
            log.info("Added like to reviewId: {}", reviewId);
        }
    }

    @Transactional
    public void initBadgeAccumulationPoint(User user) {
        BadgeAccumulationPoint badgeAccumulationPoint = BadgeAccumulationPoint.builder()
                .user(user)
                .accPoint(0L)
                .build();

        badgeAccumulationPointRepository.save(badgeAccumulationPoint);
    }

    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

//        List<String> fcmTokens = user.getFcmTokens()
//                .stream()
//                .map(FcmToken::getToken)
//                .toList();

        MatchUp matchUpNow = matchUpRepository.findOneMatchUpByStatus(MatchUpStatus.NOW)
                .orElse(null);

        Long matchUpId = matchUpNow != null ? matchUpNow.getId() : null;

        return new UserResponse(user.getId(), user.getNickname(), user.getProfileImage(), user.getCategory().getId(), matchUpId);
    }

    @Transactional
    public void deleteUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        rewardHistoryRepository.deleteByUserId(userId);
        awss3Service.deleteImage(user.getProfileImage());
        userRepository.deleteById(userId);
    }

    @Transactional
    public void togglePushNotification(Long userId, FcmTokenRequest fcmTokenRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));

        boolean newPushEnabled = !user.isPushEnabled();
        user.updatePushNotification(newPushEnabled);

        if (newPushEnabled) {
            // 푸쉬 알림이 켜질 경우 FCM 토큰 생성 및 저장
            try {
                String fcmToken = fcmTokenRequest.getToken();
                if (fcmToken == null || fcmToken.isEmpty()) {
                    throw new IllegalArgumentException("푸쉬 알림을 활성화하려면 FCM 토큰이 필요합니다.");
                }
                fcmTokenService.saveFcmToken(userId, fcmToken);
            } catch (Exception e) {
                log.error("푸쉬 알림 활성화 중 FCM 토큰 저장에 실패했습니다. userId: {}", userId, e);
                throw new RuntimeException("Failed to save FCM token.");
            }
        } else {
            // 푸쉬 알림이 꺼질 경우 FCM 토큰 삭제
            try {
                fcmTokenService.deleteFcmToken(userId);
            } catch (Exception e) {
                log.error("푸쉬 알림 비활성화 중 FCM 토큰 삭제에 실패했습니다. userId: {}", userId, e);
            }
        }
    }

}


