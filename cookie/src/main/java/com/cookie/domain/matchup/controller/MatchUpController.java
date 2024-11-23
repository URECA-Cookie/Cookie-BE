package com.cookie.domain.matchup.controller;

import com.cookie.domain.matchup.dto.response.MatchUpHistoryResponse;
import com.cookie.domain.matchup.service.MatchUpService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchups")
@RequiredArgsConstructor
public class MatchUpController {

    private final MatchUpService matchUpService;

    @GetMapping("/history")
    public ApiSuccess<?> getMatchUpHistoryList() {
        // TODO: userId JWT 토큰으로 변경
        List<MatchUpHistoryResponse> matchUpHistory = matchUpService.getMatchUpHistoryList();
        return ApiUtil.success(matchUpHistory);
    }

}
