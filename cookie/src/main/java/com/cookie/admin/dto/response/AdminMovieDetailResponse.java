package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMovieDetailResponse {
    private Long movieId;
    private String title;
    private MovieCasts director;
    private Integer runtime;
    private String posterPath;
    private String releaseDate;
    private String certification;
    private String country;
    private String plot;
    private String youtube;
    private List<String> stillCuts;
    private List<MovieCasts> actors;
    private List<String> categories;
}
