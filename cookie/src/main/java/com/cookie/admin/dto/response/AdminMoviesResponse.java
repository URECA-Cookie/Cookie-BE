package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMoviesResponse {

    private Integer currentPage;
    private List<MoviesResponse> results;
    private Integer totalPages;
}
