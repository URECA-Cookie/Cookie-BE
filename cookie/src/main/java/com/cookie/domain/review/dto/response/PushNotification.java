package com.cookie.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PushNotification {
    private Long movieId;
    private String movieTitle;
    private String writerNickname; // 리뷰 작성자 닉네임

}
