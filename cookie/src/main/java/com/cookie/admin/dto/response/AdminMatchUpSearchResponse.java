package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AdminMatchUpSearchResponse {

    private String title;
    private String posterPath;

    public AdminMatchUpSearchResponse(String title, String posterPath) {
        this.title = title;
        this.posterPath = posterPath;
    }
}
