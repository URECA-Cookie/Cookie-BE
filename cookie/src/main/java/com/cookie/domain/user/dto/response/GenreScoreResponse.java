package com.cookie.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreScoreDto {
    private Long id;           // 유저장르점수 ID
    private Long userId;       // 유저ID
    private Long romance;      // 로맨스 점수
    private Long horror;       // 공포 점수
    private Long comedy;       // 코미디 점수
    private Long action;       // 액션 점수
    private Long fantasy;      // 판타지 점수
    private Long animation;    // 애니메이션 점수
    private Long crime;        // 범죄 점수
    private Long sf;           // SF 점수
    private Long music;        // 음악 점수
    private Long thriller;     // 스릴러 점수
    private Long queer;        // 퀴어 점수
    private Long war;          // 전쟁 점수
    private Long documentary;  // 다큐멘터리 점수
}
