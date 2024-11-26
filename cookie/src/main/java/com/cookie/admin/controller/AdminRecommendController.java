package com.cookie.admin.controller;

import com.cookie.admin.dto.response.RecommendResponse;
import com.cookie.admin.service.recommend.AdminRecommendService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommend")
public class AdminRecommendController {

    private final AdminRecommendService adminRecommendService;

    @GetMapping()
    public ApiSuccess<?> getRecommendMovies() {
        List<RecommendResponse> data = adminRecommendService.getRecommendMovies();
        return ApiUtil.success(data);
    }

    @PostMapping()
    public ApiSuccess<?> recommendMovies(@RequestBody Set<Long> movieIds) {
        adminRecommendService.recommendMovies(movieIds);
        return ApiUtil.success("SUCCESS");
    }
}
