package com.cookie.domain.search.controller;

import com.cookie.domain.search.dto.request.SearchRequest;
import com.cookie.domain.search.dto.response.SearchMovieMonthRankingResponse;
import com.cookie.domain.search.service.SearchMovieMonthRankingService;
import com.cookie.domain.search.service.SearchService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "검색", description = "검색 API")
@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final SearchMovieMonthRankingService searchMovieMonthRankingService;

    @GetMapping("/api/search")
    public ResponseEntity<?> search(SearchRequest searchRequest) {

        Pageable pageable = PageRequest.of(searchRequest.getPage(), 10);
        String keyword = searchRequest.getKeyword();

        switch (searchRequest.getType().toLowerCase()) {
            case "movie":
                return ResponseEntity.ok(searchService.searchMovies(keyword, pageable));
            case "actor":
                return ResponseEntity.ok(searchService.searchActors(keyword, pageable));
            case "director":
                return ResponseEntity.ok(searchService.searchDirectors(keyword, pageable));
            default:
                return ResponseEntity.badRequest()
                        .body(ApiUtil.error(400, "INVALID_TYPE: Use 'movie', 'actor', or 'director'"));
        }
    }

    @Operation(summary = "검색 디폴트 10개 영화 리스트", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SearchMovieMonthRankingResponse.class))))
    })
    @GetMapping("/api/search/default")
    public ApiSuccess<?> getMoviesMonthRanking() {
        List<SearchMovieMonthRankingResponse> data = searchMovieMonthRankingService.getMoviesMonthRanking();
        return ApiUtil.success(data);
    }
}
