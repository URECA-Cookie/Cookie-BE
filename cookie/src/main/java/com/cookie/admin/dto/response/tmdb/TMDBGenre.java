package com.cookie.admin.dto.response.tmdb;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TMDBGenre {

    private int id;
    private String name;
}
