package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieMatchInfoForUpdate {

    private Long movieId;
    private String poster;
    private String movieTitle;
    private long voteCount;
}
