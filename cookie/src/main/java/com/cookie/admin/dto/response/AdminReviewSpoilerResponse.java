package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewSpoilerResponse {

    private Long reviewId;
    private boolean isSpoiler;
}
