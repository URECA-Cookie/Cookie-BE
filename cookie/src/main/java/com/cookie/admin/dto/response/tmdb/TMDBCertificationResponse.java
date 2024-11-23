package com.cookie.admin.dto.response.tmdb;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TMDBCertificationResponse {

    private List<TMDBMovieCertification> results;
}
