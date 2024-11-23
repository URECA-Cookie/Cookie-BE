package com.cookie.domain.matchup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharmPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchup_movie_id")
    private MatchUpMovie matchUpMovie;

    private long ost;
    private long directing;
    private long story;
    private long dialogue;
    private long visual;
    private long acting;
    private long specialEffects;

    @Builder
    public CharmPoint(MatchUpMovie matchUpMovie, long ost, long directing, long story, long dialogue, long visual, long acting, long specialEffects) {
        this.matchUpMovie = matchUpMovie;
        this.ost = ost;
        this.directing = directing;
        this.story = story;
        this.dialogue = dialogue;
        this.visual = visual;
        this.acting = acting;
        this.specialEffects = specialEffects;
    }

    public void updatePoints(long ost, long directing, long story, long dialogue, long visual, long acting, long specialEffects) {
        this.ost += ost;
        this.directing += directing;
        this.story += story;
        this.dialogue += dialogue;
        this.visual += visual;
        this.acting += acting;
        this.specialEffects += specialEffects;
    }
}
