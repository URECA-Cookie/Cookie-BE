package com.cookie.domain.user.dto.response;

import com.cookie.domain.review.dto.response.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDto {
    private List<BadgeDto> badge; // ["로맨스 마니아", "액션 마니아", "SF 마니아"]
    private List<GenreScoreDto> genreScores; // [{”로맨스”: 8}, {”액션” : 9}, {”SF” : 0}, ...]
    private List<ReviewDto> reviews; // 리뷰 리스트
}
