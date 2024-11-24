package com.cookie.domain.user.entity;

import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.MatchUpMovie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUpParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchup_movie_id")
    private MatchUpMovie matchUpMovie;

    @Builder
    public MatchUpParticipation(User user, MatchUpMovie matchUpMovie) {
        this.user = user;
        this.matchUpMovie = matchUpMovie;
    }
}
