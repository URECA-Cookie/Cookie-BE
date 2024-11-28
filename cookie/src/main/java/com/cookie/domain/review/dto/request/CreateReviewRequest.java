package com.cookie.domain.review.dto.request;


import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private long movieId;
    private String content;
    private Integer movieScore;
    private boolean isSpoiler;

    public Review toEntity(User user, Movie movie) {
        return Review.builder()
                .movie(movie)
                .user(user)
                .content(this.content)
                .movieScore(this.movieScore)
                .isHide(false)
                .isSpoiler(this.isSpoiler)
                .reviewLike(0)
                .build();
    }
}
