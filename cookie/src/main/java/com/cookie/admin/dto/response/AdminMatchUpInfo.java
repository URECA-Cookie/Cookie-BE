package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMatchUpInfo {

    private Long matchId;
    private String matchUpTitle;
    private List<MovieMatchInfo> movieMatchInfo;
    private String startTime;
    private String endTime;
    private String winner;
}
