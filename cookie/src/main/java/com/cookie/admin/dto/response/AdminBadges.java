package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBadges {

    private Long BadgeId;
    private String badgeName;
    private String genre;
    private String badgeImage;
    private Integer needPoint;
}
