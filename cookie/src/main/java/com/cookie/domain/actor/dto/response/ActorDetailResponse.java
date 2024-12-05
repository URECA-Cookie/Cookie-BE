package com.cookie.domain.actor.dto.response;

import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.dto.response.PersonDetailMovieInfo;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorDetailResponse {
    private String name;
    private String profileImage;
    private List<MovieSimpleResponse> actorMovieList;
}
