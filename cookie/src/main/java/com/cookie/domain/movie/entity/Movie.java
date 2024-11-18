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
    private String title;
    private String poster;
    @Lob
    private String plot;
    private String company;
    private LocalDateTime releasedAt;
    private int runtime;
    private double score;;
    private Rating rating;

    @Builder
    public Movie(String title, String poster, String plot, String company, LocalDateTime releasedAt, int runtime, double score, Rating rating) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.company = company;
        this.releasedAt = releasedAt;
        this.runtime = runtime;
        this.score = score;
        this.rating = rating;
    }
}
