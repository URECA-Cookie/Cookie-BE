package com.cookie.domain.director.entity;

import com.cookie.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tmdbCasterId;
    private String name;
    private String profileImage;

    @OneToMany(mappedBy = "director")
    @JsonIgnore
    private List<Movie> movies = new ArrayList<>();

    @Builder
    public Director(Long id, Long tmdbCasterId, String name, String profileImage, List<Movie> movies) {
        this.id = id;
        this.tmdbCasterId = tmdbCasterId;
        this.name = name;
        this.profileImage = profileImage;
        this.movies = movies;
    }
}
