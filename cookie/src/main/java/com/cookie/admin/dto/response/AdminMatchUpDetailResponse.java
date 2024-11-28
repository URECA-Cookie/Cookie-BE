package com.cookie.admin.dto.response;

import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.entity.enums.MatchUpType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMatchUpDetailResponse {

    private String matchUpTitle;
    private List<MovieMatchInfoForUpdate> movieMatchInfo;
    private MatchUpType matchUpType;
    private String startTime;
    private String endTime;
    private String createdAt;
    private MatchUpStatus matchUpStatus;
}
