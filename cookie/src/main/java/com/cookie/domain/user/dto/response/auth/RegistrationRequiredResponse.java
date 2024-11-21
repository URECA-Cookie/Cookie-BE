package com.cookie.domain.user.dto.response.auth;

import com.cookie.domain.user.entity.enums.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequiredResponse {
    private final SocialProvider socialProvider;
    private final String socialId;
    private final String email;
}
