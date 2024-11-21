package com.cookie.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieVideoResponse {
    private String url;   // 비디오 URL
    private String title; // 비디오 제목
}
