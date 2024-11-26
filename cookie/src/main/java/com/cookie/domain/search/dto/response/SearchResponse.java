package com.cookie.domain.search.dto.response;

import com.cookie.domain.actor.entity.Actor;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class SearchResponse {
    private Page<Movie> movies;
    private Page<Actor> actors;
    private Page<Director> directors;
}
