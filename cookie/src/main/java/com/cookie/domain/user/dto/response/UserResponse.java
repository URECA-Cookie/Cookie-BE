package com.cookie.domain.user.dto.response;

import com.cookie.domain.category.entity.Category;
import com.cookie.domain.user.dto.response.auth.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String nickname;
    private String profileImage;
    private Long genreId;
}
