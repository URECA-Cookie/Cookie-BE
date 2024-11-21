package com.cookie.domain.user.dto.response;

import com.cookie.domain.badge.dto.MyBadgeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileDataResponse {
    private String profileImage;
    private List<MyBadgeResponse> badges;
    private String nickname;
}
