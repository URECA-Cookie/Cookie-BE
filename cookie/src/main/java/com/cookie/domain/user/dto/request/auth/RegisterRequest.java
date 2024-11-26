package com.cookie.domain.user.dto.request.auth;

import com.cookie.domain.user.entity.enums.SocialProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private SocialProvider socialProvider;
    private String socialId;
    private String email;
    private String nickname;
    private String profileImage;
    private boolean pushEnabled;
    private boolean emailEnabled;
    private Long genreId;
}