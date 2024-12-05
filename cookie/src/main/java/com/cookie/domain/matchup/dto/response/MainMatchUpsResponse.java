package com.cookie.domain.matchup.dto.response;

import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.enums.MatchUpType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainMatchUpsResponse {
    private List<MainMatchUpResponse> matchUps;
    private boolean access;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainMatchUpResponse {
        private Long matchUpId;
        private String matchUpTitle;
        private MatchUpType type;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private MainMatchUpMovieResponse movie1;
        private MainMatchUpMovieResponse movie2;

        public static MainMatchUpResponse fromEntity(MatchUp matchUp) {
            return new MainMatchUpResponse(
                    matchUp.getId(),
                    matchUp.getTitle(),
                    matchUp.getType(),
                    matchUp.getStartAt(),
                    matchUp.getEndAt(),
                    new MainMatchUpMovieResponse(matchUp.getMovie1().getMovieTitle(), matchUp.getMovie1().getMoviePoster()),
                    new MainMatchUpMovieResponse(matchUp.getMovie2().getMovieTitle(), matchUp.getMovie2().getMoviePoster())
            );
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainMatchUpMovieResponse {
        private String movieTitle;
        private String moviePoster;
    }
}
