package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMovieLikesResponse {

    private String title;
    private String posterPath;
    private Integer likeAmount;
    private List<AdminReviewDetailLikesResponse> movieLikeUsers;
}
