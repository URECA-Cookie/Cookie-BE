package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewDetailLikesResponse {

    private Long likeId;
    private String username;
    private String userProfile;
    private String createdAt;
}
