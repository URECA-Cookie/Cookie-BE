package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewHideResponse {

    private Long reviewId;
    private boolean isHide;
}
