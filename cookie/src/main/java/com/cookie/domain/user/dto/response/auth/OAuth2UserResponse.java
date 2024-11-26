package com.cookie.domain.user.dto.response.auth;

import com.cookie.domain.user.entity.enums.SocialProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2UserResponse {
    private SocialProvider socialProvider;
    private String email;
    private String socialId;
    private String role;
    private String nickname;
    private boolean isRegistrationRequired;
}
