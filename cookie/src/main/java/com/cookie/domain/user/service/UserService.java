package com.cookie.domain.user.service;

import com.cookie.domain.badge.dto.MyBadgeResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.user.dto.request.MyProfileRequest;
import com.cookie.domain.user.dto.response.*;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.BadgeAccumulationPoint;
import com.cookie.domain.user.entity.GenreScore;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.UserBadge;
import com.cookie.domain.user.repository.BadgeAccumulationPointRepository;
import com.cookie.domain.user.repository.GenreScoreRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.repository.UserBadgeRepository;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserBadgeRepository userBadgeRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BadgeAccumulationPointRepository badgeAccumulationPointRepository;


    public MyPageResponse getMyPage(Long userId) {
        // 1. 유저의 닉네임 및 프로필 이미지 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        String nickname = user.getNickname();
        String profileImage = user.getProfileImage();

        // 2. 유저의 뱃지 조회
        List<BadgeResponse> badgeDtos = getAllBadgesByUserId(userId);

        // 3. 유저의 장르 점수 조회
        List<GenreScoreResponse> genreScoreDtos = getGenreScoresByUserId(userId);

        // 4. 유저의 리뷰 조회
        List<ReviewResponse> reviewDtos = getReviewsByUserId(userId);

        // 5. MyPageResponse 생성 및 반환
        return MyPageResponse.builder()
                .nickname(nickname)
                .profileImage(profileImage)
                .badge(badgeDtos)
                .genreScores(genreScoreDtos)
                .reviews(reviewDtos)
                .build();
    }



    /**
     * 유저가 보유한 뱃지 조회
     */
    public List<BadgeResponse> getAllBadgesByUserId(Long userId) {
        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);

        return userBadges.stream()
                .map(userBadge -> BadgeResponse.builder()
                        .name(userBadge.getBadge().getName())
                        .badgeImage(userBadge.getBadge().getBadgeImage())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 유저의 장르 점수 조회
     */
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
                        .queer(genreScore.getQueer())
                        .war(genreScore.getWar())
                        .documentary(genreScore.getDocumentary())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 유저의 리뷰 조회
     */
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findAllByUserIdWithMovie(userId);

        return reviews.stream()
                .map(ReviewResponse::fromReview)
                .toList();
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
                .build();
    }

    public void updateMyProfile(Long userId, MyProfileRequest request) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. 프로필 이미지와 닉네임 업데이트
        user.setProfileImage(request.getProfileImage());
        user.setNickname(request.getNickname());

        // 3. 유저의 모든 뱃지 조회
        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);

        // 4. mainBadge 처리
        userBadges.forEach(userBadge -> {
            if (userBadge.getBadge().getName().equals(request.getMainBadge())) {
                userBadge.setMain(true); // mainBadge로 설정
            } else if (userBadge.isMain()) {
                userBadge.setMain(false); // 기존 mainBadge 해제
            }
        });

        // 5. 유저 정보와 뱃지 업데이트
        userRepository.save(user);
        userBadgeRepository.saveAll(userBadges);
    }

    public BadgeAccResponse getBadgeAccumulationPoint(Long userId) {
        // BadgeAccumulationPoint 데이터를 가져옴
        BadgeAccumulationPoint badgeAccumulationPoint = badgeAccumulationPointRepository.findByUserId(userId);

        // BadgeAccResponse로 변환하여 반환
        return BadgeAccResponse.builder()
                .romancePoint(badgeAccumulationPoint.getRomancePoint())
                .horrorPoint(badgeAccumulationPoint.getHorrorPoint())
                .comedyPoint(badgeAccumulationPoint.getComedyPoint())
                .actionPoint(badgeAccumulationPoint.getActionPoint())
                .fantasyPoint(badgeAccumulationPoint.getFantasyPoint())
                .animationPoint(badgeAccumulationPoint.getAnimationPoint())
                .crimePoint(badgeAccumulationPoint.getCrimePoint())
                .sfPoint(badgeAccumulationPoint.getSfPoint())
                .musicPoint(badgeAccumulationPoint.getMusicPoint())
                .thrillerPoint(badgeAccumulationPoint.getThrillerPoint())
                .queerPoint(badgeAccumulationPoint.getQueerPoint())
                .warPoint(badgeAccumulationPoint.getWarPoint())
                .documentaryPoint(badgeAccumulationPoint.getDocumentaryPoint())
                .build();
    }
}
