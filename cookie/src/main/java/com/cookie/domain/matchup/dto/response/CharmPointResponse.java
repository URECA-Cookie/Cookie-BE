package com.cookie.domain.matchup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CharmPointResponse {
    private long ost;
    private long direction;
    private long story;
    private long dialogue;
    private long visual;
    private long acting;
    private long specialEffect;

    public CharmPointResponse calculateProportions() {
        long total = ost + direction + story + dialogue + visual + acting + specialEffect;

        if (total == 0) {
            return new CharmPointResponse(0, 0, 0, 0, 0, 0, 0);
        }

        return new CharmPointResponse(
                (ost * 100) / total,
                (direction * 100) / total,
                (story * 100) / total,
                (dialogue * 100) / total,
                (visual * 100) / total,
                (acting * 100) / total,
                (specialEffect * 100) / total
        );
    }
}
