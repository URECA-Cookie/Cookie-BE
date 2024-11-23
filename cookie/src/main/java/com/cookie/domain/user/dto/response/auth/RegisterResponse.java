package com.cookie.domain.user.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private final String token;
}