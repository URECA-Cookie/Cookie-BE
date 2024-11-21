package com.cookie.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;      // 리뷰 ID
    private Long userId;        // 유저 ID
    private Long movieId;       // 영화 ID
    private String content;     // 리뷰 내용
    private Double movieScore;  // 영화 평점
    private Boolean isHide;     // 숨김 여부
    private Boolean isSpoiler;  // 스포일러 여부
    private Long reviewLike;    // 좋아요 수
    private LocalDate createdAt; // 생성일
    private LocalDate updatedAt; // 수정일
    private String movieTitle;   // 영화 제목
    private String moviePoster;  // 영화 포스터
}

