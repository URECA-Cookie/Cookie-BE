package com.cookie.domain.user.dto.response;

import com.cookie.domain.review.dto.response.MovieReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewUserResponse {
    private String nickname;
    private String profileImage;
    private String mainBadgeImage;
    private String mainBadgeName;
}
