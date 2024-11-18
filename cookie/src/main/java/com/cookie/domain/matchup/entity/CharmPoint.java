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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchup_id")
    private MatchUp matchUp;

    private long ost;
    private long directing;
    private long story;
    private long dialogue;
    private long visual;
    private long acting;
    private long specialEffects;

    @Builder
    public CharmPoint(MatchUp matchUp, long ost, long directing, long story, long dialogue, long visual, long acting, long specialEffects) {
        this.matchUp = matchUp;
        this.ost = ost;
        this.directing = directing;
        this.story = story;
        this.dialogue = dialogue;
        this.visual = visual;
        this.acting = acting;
        this.specialEffects = specialEffects;
    }
}
