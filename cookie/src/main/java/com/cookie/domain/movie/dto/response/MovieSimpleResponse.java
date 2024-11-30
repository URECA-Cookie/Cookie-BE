package com.cookie.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSimpleResponse {
    private Long id;
    private String title;
    private String poster;
    private String releasedAt;
    private String country;
    private Long likes;
    private Long reviews;
}
