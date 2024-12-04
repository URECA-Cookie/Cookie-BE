package com.cookie.domain.user.dto.response;

import com.cookie.domain.user.dto.response.auth.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private UserResponse user;
    private TokenResponse token;
}
