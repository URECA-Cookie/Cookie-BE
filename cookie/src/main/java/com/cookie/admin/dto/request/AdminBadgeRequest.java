package com.cookie.admin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBadgeRequest {

    private String badgeName;
    private String genre;
    private Integer needPoint;
}
