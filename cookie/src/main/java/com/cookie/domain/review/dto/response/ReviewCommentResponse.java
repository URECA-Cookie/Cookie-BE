package com.cookie.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentResponse {
    private String movieTitle;   // 영화 제목
    private String reviewContent; // 리뷰 내용
    private String commentContent; // 댓글 내용
}
