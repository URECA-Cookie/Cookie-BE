package com.cookie.domain.search.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MovieMonthTMDB {

    private Long id;
    private String title;
    private Double popularity;

    @JsonCreator
    public MovieMonthTMDB(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("popularity") Double popularity
            ) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
    }
}

