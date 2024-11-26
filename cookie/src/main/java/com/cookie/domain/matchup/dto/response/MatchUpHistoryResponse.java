package com.cookie.domain.matchup.dto.response;

import com.cookie.domain.matchup.entity.enums.MatchUpType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchUpHistoryResponse {
    private Long matchUpId;
    private String matchUpTitle;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
