package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TMDBMovieCertification {

    @JsonProperty("iso_3166_1")
    private String iso31661;

    @JsonProperty("release_dates")
    private List<TMDBCertification> releaseDates;
}
