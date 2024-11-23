package com.cookie.domain.matchup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionPointResponse {
    private long touching;
    private long angry;
    private long joy;
    private long immersion;
    private long excited;
    private long empathy;
    private long tension;

    public EmotionPointResponse calculateProportions() {
        long total = touching + angry + joy + immersion + excited + empathy + tension;

        if (total == 0) {
            return new EmotionPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new EmotionPointResponse(
                (touching * 100) / total,
                (angry * 100) / total,
                (joy * 100) / total,
                (immersion * 100) / total,
                (excited * 100) / total,
                (empathy * 100) / total,
                (tension * 100) / total
        );
    }
}
