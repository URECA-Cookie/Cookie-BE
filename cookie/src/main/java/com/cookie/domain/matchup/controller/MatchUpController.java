package com.cookie.domain.matchup.controller;

import com.cookie.domain.matchup.dto.request.MatchUpVoteRequest;
import com.cookie.domain.matchup.dto.response.MatchUpHistoryDetailResponse;
import com.cookie.domain.matchup.dto.response.MatchUpHistoryResponse;
import com.cookie.domain.matchup.dto.response.MatchUpResponse;
import com.cookie.domain.matchup.service.MatchUpService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/matchups")
@RequiredArgsConstructor
public class MatchUpController {

    private final MatchUpService matchUpService;

    @GetMapping("/history")
    public ApiSuccess<?> getMatchUpHistoryList() {
        List<MatchUpHistoryResponse> matchUpHistory = matchUpService.getMatchUpHistoryList();
        return ApiUtil.success(matchUpHistory);
    }

    @GetMapping("/{matchUpId}/history")
    public ApiSuccess<?> getMatchUpHistoryDetail(@PathVariable(name = "matchUpId") Long matchUpId) {
        MatchUpHistoryDetailResponse matchUpHistoryDetail = matchUpService.getMatchUpHistoryDetail(matchUpId);
        return ApiUtil.success(matchUpHistoryDetail);
    }

    @PostMapping("/{matchUpId}/movies/{matchUpMovieId}/vote")
    public ApiSuccess<?> addMatchUpVote(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "matchUpId") Long matchUpId, @PathVariable(name = "matchUpMovieId") Long matchUpMovieId, @RequestBody MatchUpVoteRequest matchUpVoteRequest) {
        Long userId = customOAuth2User.getId();
        matchUpService.addMatchUpVote(userId, matchUpId, matchUpMovieId, matchUpVoteRequest);
        return ApiUtil.success("SUCCESS");
    }

    @GetMapping("{matchUpId}")
    public ApiSuccess<?> getOnGoingMatchUp(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "matchUpId") Long matchUpId) {
        log.info("매치업 상세보기 시작");
        Long userId = customOAuth2User.getId();
        MatchUpResponse matchUp = matchUpService.getOnGoingMatchUp(matchUpId, userId);
        return ApiUtil.success(matchUp);
    }

}
