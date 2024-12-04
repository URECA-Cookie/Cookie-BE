package com.cookie.domain.search.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MovieDateTimeTMDBResponse {

    private List<MovieDateTimeTMDB> results;
}
