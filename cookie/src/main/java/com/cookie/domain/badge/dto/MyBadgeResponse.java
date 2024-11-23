package com.cookie.domain.badge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBadgeResponse {
    private String name;
    private String badgeImage;
    private boolean isMain;
}
