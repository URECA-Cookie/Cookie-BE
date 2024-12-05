package com.cookie.admin.controller;

import com.cookie.admin.dto.request.AdminMatchUpRequest;
import com.cookie.admin.dto.response.AdminMatchUpDetailResponse;
import com.cookie.admin.dto.response.AdminMatchUpSearchResponse;
import com.cookie.admin.dto.response.AdminMatchUpsResponse;
import com.cookie.admin.service.matchUp.AdminMatchUpService;
import com.cookie.admin.service.movie.TMDBService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin 매치 업 관리", description = "Admin 매치 업 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/match-up")
public class AdminMatchUpController {

    private final AdminMatchUpService adminMatchUpService;
    private final TMDBService tmdbService;

    @Operation(summary = "영화 매치 업 등록 시 영화 검색 By TMDB", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = AdminMatchUpSearchResponse.class))))
    })
    @GetMapping("/{movieName}")
    public ApiSuccess<?> getMovieTitleWithPosterByTMDB(@PathVariable("movieName") String movieName) {
        List<AdminMatchUpSearchResponse> data = tmdbService.getMoviesByNameForMatchUp(movieName);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 매치 업 현황 리스트 조회", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminMatchUpsResponse.class)))
    })
    @GetMapping()
    public ApiSuccess<?> getMatchUps() {
        AdminMatchUpsResponse data = adminMatchUpService.getMatchUps();
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 매치 업 상세 조회", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = AdminMatchUpDetailResponse.class))))
    })
    @GetMapping("/detail/{matchUpId}")
    public ApiSuccess<?> getMatchUpDetail(@PathVariable("matchUpId") Long matchUpId) {
        AdminMatchUpDetailResponse data = adminMatchUpService.getMatchUpDetail(matchUpId);
        return ApiUtil.success(data);
    }

    @Operation(summary = "영화 매치 업 생성", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping()
    public ApiSuccess<?> createMatchUp(@RequestBody AdminMatchUpRequest request) {
        adminMatchUpService.createMatchUp(request);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "영화 매치 업 업데이트",
            parameters = {
                    @Parameter(name = "AdminMatchUpRequest", description = "영화 수정 시 2개 전부 요청 필요",
                            schema = @Schema(implementation = AdminMatchUpRequest.class, nullable = true))
            },
            responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PutMapping("/{matchUpId}")
    public ApiSuccess<?> updateMatchUp(@PathVariable("matchUpId") Long matchUpId,
                                       @RequestBody AdminMatchUpRequest update) {
        adminMatchUpService.updateMatchUp(matchUpId, update);
        return ApiUtil.success("SUCCESS");
    }

    @Operation(summary = "영화 매치 업 삭제", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @DeleteMapping("/{matchUpId}")
    public ApiSuccess<?> deleteMatchUp(@PathVariable("matchUpId") Long matchUpId) {
        adminMatchUpService.deleteMatchUp(matchUpId);
        return ApiUtil.success("SUCCESS");
    }
}

