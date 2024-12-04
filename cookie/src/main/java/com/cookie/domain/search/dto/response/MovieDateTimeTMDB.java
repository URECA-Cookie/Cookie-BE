package com.cookie.domain.search.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieDateTimeTMDB {

    private Long id;
    private String title;
    private Double popularity;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonCreator
    public MovieDateTimeTMDB(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("popularity") Double popularity,
            @JsonProperty("original_language") String originalLanguage
            ) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.originalLanguage = originalLanguage;
    }
}

