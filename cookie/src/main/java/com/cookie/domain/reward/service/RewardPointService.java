package com.cookie.domain.reward.service;

import com.cookie.domain.badge.entity.Badge;
import com.cookie.domain.badge.repository.BadgeRepository;
import com.cookie.domain.user.entity.BadgeAccumulationPoint;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.UserBadge;
import com.cookie.domain.user.repository.BadgeAccumulationPointRepository;
import com.cookie.domain.user.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RewardPointService {

    private final RewardCacheService rewardCacheService;

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeAccumulationPointRepository badgeAccumulationPointRepository;
    private final BadgeRepository badgeRepository;

    @Transactional
    public void updateBadgePointFromReview(User user, Long actionId) {

        Long actionPoint = rewardCacheService.getActionPointByCache(actionId);

        BadgeAccumulationPoint userPoint = badgeAccumulationPointRepository.findByUserId(user.getId());
        Long currentUserPoint = userPoint.getAccPoint();
        Long sumPoint = currentUserPoint + actionPoint;

        userPoint.updateAccPoint(sumPoint);

        checkAndAssignBadge(user, sumPoint);
    }

    private void checkAndAssignBadge(User user, Long sumPoint) {

        Map<String, Long> badgeNeedPoints = Map.of(
                "normal", rewardCacheService.getBadgeNeedPointByGrade("normal"),
                "rare", rewardCacheService.getBadgeNeedPointByGrade("rare"),
                "epic", rewardCacheService.getBadgeNeedPointByGrade("epic")
        );

        for (Map.Entry<String, Long> entry : badgeNeedPoints.entrySet()) {
            String grade = entry.getKey();
            Long needPoint = entry.getValue();

            if (sumPoint >= needPoint && !hasBadge(user.getId(), grade)) {
                assignBadgeToUser(user, grade);
                break;
            }
        }
    }

    private boolean hasBadge(Long userId, String grade) {
        return userBadgeRepository.findByUserIdAAndBadgeGrade(userId, grade).isPresent();
    }

    private void assignBadgeToUser(User user, String grade) {

        Badge badge = badgeRepository.findBadgeByGrade(grade)
                .orElseThrow(() -> new IllegalArgumentException("해당 등급의 뱃지가 존재하지 않습니다."));

        UserBadge userBadge = UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .isMain(false)
                        .build();

        userBadgeRepository.save(userBadge);
    }
}

