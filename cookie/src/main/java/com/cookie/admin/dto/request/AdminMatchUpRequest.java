package com.cookie.admin.dto.request;

import com.cookie.admin.dto.response.MovieMatchInfo;
import com.cookie.domain.matchup.entity.enums.MatchUpType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class AdminMatchUpRequest {

    private String matchTitle;
    private List<MovieMatchInfo> matchUpMovies;
    private MatchUpType matchUpType;

    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private LocalDateTime endTime;
}
