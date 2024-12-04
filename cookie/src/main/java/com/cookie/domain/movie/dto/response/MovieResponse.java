package com.cookie.domain.movie.dto.response;

import com.cookie.domain.actor.dto.response.ActorResponse;
import com.cookie.domain.director.dto.response.DirectorResponse;
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
public class MovieResponse {
    private Long id;                   // 영화 ID
    private String title;              // 제목
    private String poster;             // 포스터 이미지
    private String plot;               // 줄거리
    private String releasedAt;         // 개봉일 (LocalDateTime -> String 변환)
    private Integer runtime;           // 상영시간 (Integer)
    private Double score;              // 평점
    private Long likes;
    private String certification;      // 연령등급 (Enum -> String 변환)
    private List<String> images;       // 영화 이미지 리스트
    private String  videos; // 영화 비디오 리스트
    private String country;    // 제작 국가 리스트
    private DirectorResponse director; //감독 정보
    private List<ActorResponse> actors;
    private List<ReviewResponse> reviews;
    private boolean isLiked;

}
