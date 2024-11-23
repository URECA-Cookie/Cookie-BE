package com.cookie.domain.matchup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUpMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String movieTitle;
    private String moviePoster;
    private long voteCount;
    private boolean win;

    @OneToOne(mappedBy = "matchUpMovie", fetch = FetchType.LAZY)
    private CharmPoint charmPoint;

    @OneToOne(mappedBy = "matchUpMovie", fetch = FetchType.LAZY)
    private EmotionPoint emotionPoint;

    public void incrementVoteCount() {
        this.voteCount++;
    }
}
