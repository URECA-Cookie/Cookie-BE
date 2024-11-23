package com.cookie.domain.movie.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long TMDBMovieId;
    private String title;
    private String poster;
    @Lob
    private String plot;
    private String releasedAt;
    private Integer runtime;
    private double score;
    private String certification;

    @Builder
    public Movie(Long id, Long TMDBMovieId, String title, String poster, String plot,
                 String releasedAt, Integer runtime, double score, String certification) {
        this.id = id;
        this.TMDBMovieId = TMDBMovieId;
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.releasedAt = releasedAt;
        this.runtime = runtime;
        this.score = score;
        this.certification = certification;
    }
}
