package com.cookie.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUserResponse {
    private Long userId;
    private String nickname;
    private String profileImage;
}
