package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MoviesResponse {
    private Long movieId;
    private String title;
    private String releaseDate;
    private String plot;
    private String director;
    private List<String> actors;
}
