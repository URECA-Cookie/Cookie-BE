package com.cookie.domain.search.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class MovieMonthTMDBResponse {

    private List<MovieMonthTMDB> results;
}
