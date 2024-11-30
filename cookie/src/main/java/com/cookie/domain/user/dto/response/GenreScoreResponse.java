package com.cookie.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreScoreResponse {
    private long romance;
    private long horror;
    private long comedy;
    private long action;
    private long fantasy;
    private long animation;
    private long crime;
    private long sf;
    private long music;
    private long thriller;
    private long war;
    private long documentary;
    private long drama;
    private long family;
    private long history;
    private long mistery;
    private long tvMovie;
    private long western;
    private long adventure;

}
