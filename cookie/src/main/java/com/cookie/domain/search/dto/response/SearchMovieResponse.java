package com.cookie.domain.search.dto.response;

import com.cookie.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchMovieResponse {
    private Long id;
    private String title;
    private String poster;
    private String releasedAt;
    private String director;

    public static SearchMovieResponse fromMovie(Movie movie) {

        return new SearchMovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPoster(),
                movie.getReleasedAt(),
                movie.getDirector().getName()
        );
    }
}
