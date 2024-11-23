package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminMovieDeleteResponse {

    private Long movieId;
    private String message;
}
