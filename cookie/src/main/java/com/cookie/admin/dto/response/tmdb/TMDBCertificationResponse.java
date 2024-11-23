package com.cookie.admin.dto.response.tmdb;

import lombok.Getter;

import java.util.List;

@Getter
public class TMDBCertificationResponse {

    private List<TMDBMovieCertification> results;
}
