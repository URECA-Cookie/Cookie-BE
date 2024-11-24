package com.cookie.domain.matchup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchUpVoteRequest {
    private CharmPointRequest charmPoint;
    private EmotionPointRequest emotionPoint;

}
