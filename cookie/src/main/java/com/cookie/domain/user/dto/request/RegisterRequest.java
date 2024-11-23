package com.cookie.domain.user.dto.request;

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
    private boolean isPushEnabled;
    private boolean isEmailEnabled;
}