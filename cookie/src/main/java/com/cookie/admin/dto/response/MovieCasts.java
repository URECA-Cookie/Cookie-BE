package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieCasts {

    private Long casterId;
    private String name;
    private String profilePath;
}


