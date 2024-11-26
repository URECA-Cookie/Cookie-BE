package com.cookie.domain.user.dto.response;

import com.cookie.domain.badge.dto.MyBadgeResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponse {
    private String nickname;
    private String profileImage;
    private List<MyBadgeResponse> badge; // ["로맨스 마니아", "액션 마니아", "SF 마니아"]
    private List<GenreScoreResponse> genreScores; // [{”로맨스”: 8}, {”액션” : 9}, {”SF” : 0}, ...]
    private List<ReviewResponse> reviews; // 리뷰 리스트
}
