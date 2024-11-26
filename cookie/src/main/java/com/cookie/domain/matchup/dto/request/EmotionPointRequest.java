package com.cookie.domain.matchup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionPointRequest {
    private long touching;
    private long angry;
    private long joy;
    private long immersion;
    private long excited;
    private long empathy;
    private long tension;
}
