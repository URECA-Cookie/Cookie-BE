package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMatchUpsResponse {

    private List<AdminMatchUpInfo> nowMatchUps;
    private List<AdminMatchUpInfo> pendingMatchUps;
    private List<AdminMatchUpInfo> expireMatchUps;
}
