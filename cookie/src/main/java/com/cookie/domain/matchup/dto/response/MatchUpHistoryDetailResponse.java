package com.cookie.domain.matchup.dto.response;

import com.cookie.domain.matchup.entity.MatchUpMovie;
import com.cookie.domain.matchup.entity.enums.MatchUpType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchUpHistoryDetailResponse {
    private String matchUpTitle;
    private MatchUpType type;
    private LocalDateTime startAt;
    private LocalDateTime entAt;
    private MatchUpMovieResponse movie1;
    private MatchUpMovieResponse movie2;

    public static MatchUpMovieResponse fromEntity(MatchUpMovie matchUpMovie, CharmPointResponse charmPointResponse, EmotionPointResponse emotionPointResponse) {
        return new MatchUpMovieResponse(
                matchUpMovie.getId(),
                matchUpMovie.getMovieTitle(),
                matchUpMovie.getMoviePoster(),
                matchUpMovie.getVoteCount(),
                matchUpMovie.isWin(),
                charmPointResponse,
                emotionPointResponse
        );
    }
}