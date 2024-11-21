package com.cookie.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    private long queerPoint;
    private long warPoint;
    private long documentaryPoint;
}
