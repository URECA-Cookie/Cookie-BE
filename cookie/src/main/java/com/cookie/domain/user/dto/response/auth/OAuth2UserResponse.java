package com.cookie.domain.user.dto.response.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2UserResponse {
    private String role;
    private String nickname;
}
