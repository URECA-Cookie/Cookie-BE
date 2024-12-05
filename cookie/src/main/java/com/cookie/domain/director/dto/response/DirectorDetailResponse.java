package com.cookie.domain.director.dto.response;

import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.dto.response.PersonDetailMovieInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDetailResponse {
    private String name;
    private String profileImage;
    private List<MovieSimpleResponse> directorMovieList;
}
