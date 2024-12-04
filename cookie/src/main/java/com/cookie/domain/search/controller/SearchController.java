package com.cookie.domain.search.controller;

import com.cookie.domain.search.dto.request.SearchRequest;
import com.cookie.domain.search.dto.response.SearchMovieMonthRankingResponse;
import com.cookie.domain.search.service.SearchMovieMonthRankingService;
import com.cookie.domain.search.service.SearchService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/api/search/weekOrder")
    public ApiSuccess<?> getMoviesWeekOrder() {
        List<SearchMovieMonthRankingResponse> data = searchMovieMonthRankingService.getMoviesWeekOrder();
        return ApiUtil.success(data);
    }
}
