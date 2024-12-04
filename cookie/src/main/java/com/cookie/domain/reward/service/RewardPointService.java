package com.cookie.domain.reward.service;

import com.cookie.domain.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardPointService {

    private final RewardCacheService rewardCacheService;

//    public void reviewRewardPostPoint(Long userId, Movie movie, Long actionId) {
//
//        Integer actionPoint = rewardCacheService.getActionPointByCache(actionId);
//    }
//
//
//    public void matchUpRewardPostPoint() {


}

