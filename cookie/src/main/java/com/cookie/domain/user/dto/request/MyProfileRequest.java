package com.cookie.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileRequest {
    private String profileImage;
    private String nickname;
    private String mainBadge;
}
