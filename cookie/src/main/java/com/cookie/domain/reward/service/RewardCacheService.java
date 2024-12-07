package com.cookie.domain.reward.service;

import com.cookie.domain.badge.entity.BadgeAction;
import com.cookie.domain.badge.repository.BadgeActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardCacheService {

    private final BadgeActionRepository badgeActionRepository;

//    @Cacheable(value = "action", key = "'action:' + #actionId", cacheManager = "pointLivedCacheManager")
//    public Integer getActionPointByCache(Long actionId) {
//        return badgeActionRepository.findById(actionId)
//                .map(BadgeAction::getActionPoint)
//                .orElseThrow(() -> new IllegalArgumentException("해당 액션 id는 존재하지 않습니다."));
//    }
}
