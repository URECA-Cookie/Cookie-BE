package com.cookie.domain.search.service;

import com.cookie.domain.actor.entity.Actor;
import com.cookie.domain.actor.repository.ActorRepository;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.director.repository.DirectorRepository;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;

    public Page<Movie> searchMovies(String keyword, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    public Page<Actor> searchActors(String keyword, Pageable pageable) {
        return actorRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Page<Director> searchDirectors(String keyword, Pageable pageable) {
        return directorRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
}

