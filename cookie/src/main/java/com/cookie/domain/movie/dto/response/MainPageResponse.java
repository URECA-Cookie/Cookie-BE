package com.cookie.domain.movie.dto.response;

import com.cookie.domain.matchup.dto.response.MainMatchUpsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainPageResponse {
    private List<String> banners;
    private MainMatchUpsResponse matchUp;
    private BoxofficeMovieList boxOffice;
}
