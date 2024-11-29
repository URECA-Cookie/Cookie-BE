package com.cookie.domain.user.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAccResponse {
    private long romancePoint;
    private long horrorPoint;
    private long comedyPoint;
    private long actionPoint;
    private long fantasyPoint;
    private long animationPoint;
    private long crimePoint;
    private long sfPoint;
    private long musicPoint;
    private long thrillerPoint;
    private long warPoint;
    private long documentaryPoint;
    private long dramaPoint;
    private long familyPoint;
    private long historyPoint;
    private long misteryPoint;
    private long tvMoviePoint;
    private long westernPoint;
    private long adventurePoint;
}
