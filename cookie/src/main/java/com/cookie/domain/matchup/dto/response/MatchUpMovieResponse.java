package com.cookie.domain.matchup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchUpMovieResponse {
    private Long movieId;
    private String movieTitle;
    private String moviePoster;
    private long movieLike;
    private boolean win;
    private CharmPointResponse charmPoint;
    private EmotionPointResponse EmotionPoint;
}
