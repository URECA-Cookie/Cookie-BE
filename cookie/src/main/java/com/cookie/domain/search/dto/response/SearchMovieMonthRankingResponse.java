package com.cookie.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMovieMonthRankingResponse {

    private Long movieId;
    private String movieTitle;
    private String releaseAt;
    private int ranking;
    private String poster;
    private String director;
}
