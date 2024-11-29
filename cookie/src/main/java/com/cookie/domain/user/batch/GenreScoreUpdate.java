package com.cookie.domain.user.batch;

import com.cookie.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenreScoreUpdate {
    private User user;      // 점수를 업데이트할 사용자
    private String genre;   // 업데이트할 장르
    private long score;     // 업데이트할 점수
}
