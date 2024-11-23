package com.cookie.admin.dto.response.tmdb;

import lombok.Getter;

import java.util.List;

@Getter
public class TMDBMovieCredits {

    private List<TMDBMovieCreditsResponse> cast;
    private List<TMDBMovieCreditsResponse> crew;
}
