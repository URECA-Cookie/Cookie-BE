package com.cookie.admin.controller;

import com.cookie.admin.dto.response.RecommendResponse;
import com.cookie.admin.service.recommend.AdminRecommendService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Admin 영화 추천 관리", description = "Admin 영화 추천 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/recommend")
public class AdminRecommendController {

    private final AdminRecommendService adminRecommendService;


    @Operation(summary = "서비스 관리자 추천 영화 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = RecommendResponse.class))))
    })
    @GetMapping()
    public ApiSuccess<?> getRecommendMovies() {
        List<RecommendResponse> data = adminRecommendService.getRecommendMovies();
        return ApiUtil.success(data);
    }

    @Operation(summary = "추천 영화 리스트 생성", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping()
    public ApiSuccess<?> recommendMovies(@RequestBody Set<Long> movieIds) {
        adminRecommendService.recommendMovies(movieIds);
        return ApiUtil.success("SUCCESS");
    }
}
