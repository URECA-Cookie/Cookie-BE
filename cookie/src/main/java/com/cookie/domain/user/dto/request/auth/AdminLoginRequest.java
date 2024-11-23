package com.cookie.domain.user.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminLoginRequest {
    private String id;
    private String password;
}
