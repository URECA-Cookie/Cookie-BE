package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewDetailResponse {

    private Long reviewId;
    private boolean isHide;
    private boolean isSpoiler;
    private String username;
    private String userProfile;
    private String content;
    private long reviewLike;
    private Integer score;
    private String title;
    private String director;
    private String posterPath;
    private Integer commentCount;
    private String createdAt;
}
