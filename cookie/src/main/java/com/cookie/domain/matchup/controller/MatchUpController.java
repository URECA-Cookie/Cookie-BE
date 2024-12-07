package com.cookie.domain.matchup.controller;

import com.cookie.domain.matchup.dto.request.MatchUpVoteRequest;
import com.cookie.domain.matchup.dto.response.MatchUpHistoryDetailResponse;
import com.cookie.domain.matchup.dto.response.MatchUpHistoryResponse;
import com.cookie.domain.matchup.dto.response.MatchUpResponse;
import com.cookie.domain.matchup.service.MatchUpService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "매치 업", description = "매치 업 API")
@Slf4j
@RestController
@RequestMapping("/api/matchups")
@RequiredArgsConstructor
public class MatchUpController {

    private final MatchUpService matchUpService;

    @Operation(summary = "매치 업 히스토리 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = MatchUpHistoryResponse.class))))
    })
    @GetMapping("/history")
    public ApiSuccess<?> getMatchUpHistoryList() {
        List<MatchUpHistoryResponse> matchUpHistory = matchUpService.getMatchUpHistoryList();
        return ApiUtil.success(matchUpHistory);
    }

    @Operation(summary = "매치 업 히스토리 상세 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchUpHistoryDetailResponse.class)))
    })
    @GetMapping("/{matchUpId}/history")
    public ApiSuccess<?> getMatchUpHistoryDetail(@PathVariable(name = "matchUpId") Long matchUpId) {
        MatchUpHistoryDetailResponse matchUpHistoryDetail = matchUpService.getMatchUpHistoryDetail(matchUpId);
        return ApiUtil.success(matchUpHistoryDetail);
    }

    @Operation(summary = "매치 업 영화 투표", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping("/{matchUpId}/movies/{matchUpMovieId}/vote")
    public ApiSuccess<?> addMatchUpVote(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "matchUpId") Long matchUpId, @PathVariable(name = "matchUpMovieId") Long matchUpMovieId, @RequestBody MatchUpVoteRequest matchUpVoteRequest) {
        Long userId = customOAuth2User.getId();
        matchUpService.addMatchUpVote(userId, matchUpId, matchUpMovieId, matchUpVoteRequest);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "매치 업 영화 투표 현황 상세정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchUpResponse.class)))
    })
    @GetMapping("/{matchUpId}")
    public ApiSuccess<?> getOnGoingMatchUp(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "matchUpId") Long matchUpId) {
        log.info("매치업 상세보기 시작");
        Long userId = customOAuth2User.getId();
        MatchUpResponse matchUp = matchUpService.getOnGoingMatchUp(matchUpId, userId);
        return ApiUtil.success(matchUp);
    }

}
