package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewDetailCommentsResponse {

    private Long commentId;
    private String username;
    private String userProfile;
    private String content;
    private String createdAt;
}
