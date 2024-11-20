package com.cookie.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorMovie {
    private String title;
    private String poster;
    private String released;
}
