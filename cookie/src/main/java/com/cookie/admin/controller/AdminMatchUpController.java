package com.cookie.admin.controller;

import com.cookie.admin.dto.request.AdminMatchUpRequest;
import com.cookie.admin.dto.response.AdminMatchUpDetailResponse;
import com.cookie.admin.dto.response.AdminMatchUpSearchResponse;
import com.cookie.admin.dto.response.AdminMatchUpsResponse;
import com.cookie.admin.service.matchUp.AdminMatchUpService;
import com.cookie.admin.service.movie.TMDBService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/match-up")
public class AdminMatchUpController {

    private final AdminMatchUpService adminMatchUpService;
    private final TMDBService tmdbService;

    @GetMapping("/{movieName}")
    public ApiSuccess<?> getMovieTitleWithPosterByTMDB(@PathVariable("movieName") String movieName) {
        List<AdminMatchUpSearchResponse> data = tmdbService.getMoviesByNameForMatchUp(movieName);
        return ApiUtil.success(data);
    }

    @GetMapping()
    public ApiSuccess<?> getMatchUps() {
        AdminMatchUpsResponse data = adminMatchUpService.getMatchUps();
        return ApiUtil.success(data);
    }

    @GetMapping("/detail/{matchUpId}")
    public ApiSuccess<?> getMatchUpDetail(@PathVariable("matchUpId") Long matchUpId) {
        AdminMatchUpDetailResponse data = adminMatchUpService.getMatchUpDetail(matchUpId);
        return ApiUtil.success(data);
    }

    @PostMapping()
    public ApiSuccess<?> createMatchUp(@RequestBody AdminMatchUpRequest request) {
        adminMatchUpService.createMatchUp(request);
        return ApiUtil.success("SUCCESS");
    }

    @PutMapping("/{matchUpId}")
    public ApiSuccess<?> updateMatchUp(@PathVariable("matchUpId") Long matchUpId,
                                       @RequestBody AdminMatchUpRequest update) {
        adminMatchUpService.updateMatchUp(matchUpId, update);
        return ApiUtil.success("SUCCESS");
    }

    @DeleteMapping("/{matchUpId}")
    public ApiSuccess<?> deleteMatchUp(@PathVariable("matchUpId") Long matchUpId) {
        adminMatchUpService.deleteMatchUp(matchUpId);
        return ApiUtil.success("SUCCESS");
    }
}

