package com.cookie.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMovieMonthRankingResponse {

    private Long movieId;
    private String movieTitle;
    private String releaseYear;
    private int ranking;
    private String genreAgent;
    private Integer runtime;
    private String certification;
}
