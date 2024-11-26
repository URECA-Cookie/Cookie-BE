package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendResponse {

    private Long id;
    private Long movieId;
    private String title;
    private String posterPath;
}
