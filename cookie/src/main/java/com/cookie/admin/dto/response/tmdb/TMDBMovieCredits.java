package com.cookie.admin.dto.response.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TMDBMovieCredits {

    private List<TMDBMovieCreditsResponse> cast;
    private List<TMDBMovieCreditsResponse> crew;
}
