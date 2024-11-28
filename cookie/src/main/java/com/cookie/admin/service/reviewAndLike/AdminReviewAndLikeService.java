package com.cookie.admin.service.reviewAndLike;

import com.cookie.admin.dto.response.*;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewComment;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewCommentRepository;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReviewAndLikeService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final MovieLikeRepository movieLikeRepository;

    @Transactional(readOnly = true)
    public List<AdminReviewResponse> getMovieReviews(Long movieId, String dateOrder,
                                                     String likesOrder, Integer movieScoreFilter) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        List<Review> reviews = reviewRepository.findReviewsByMovieId(movieId);

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        if (movieScoreFilter != null) {
            reviews = reviews.stream()
                    .filter(review -> review.getMovieScore().equals(movieScoreFilter))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        reviews.sort(dateOrder.equals("latest") ?
                Comparator.comparing(Review::getCreatedAt).reversed() :
                Comparator.comparing(Review::getCreatedAt));

        reviews.sort(likesOrder.equals("asc") ?
                Comparator.comparing(Review::getReviewLike) :
                Comparator.comparing(Review::getReviewLike).reversed());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

        return reviews.stream().map(review -> AdminReviewResponse.builder()
                        .reviewId(review.getId())
                        .username(review.getUser().getNickname())
                        .userProfile(review.getUser().getProfileImage())
                        .content(getTrimmedContent(review.getContent()))
                        .reviewLike(review.getReviewLike())
                        .score(review.getMovieScore())
                        .isHide(review.isHide())
                        .isSpoiler(review.isSpoiler())
                        .commentCount(review.getReviewComments().size())
                        .createdAt(review.getCreatedAt().format(dateTimeFormatter))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminReviewDetailResponse getMovieReviewsDetail(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보는 존재하지 않습니다. " + reviewId));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

        return AdminReviewDetailResponse.builder()
                .reviewId(review.getId())
                .username(review.getUser().getNickname())
                .userProfile(review.getUser().getProfileImage())
                .title(review.getMovie().getTitle())
                .director(review.getMovie().getDirector().getName())
                .posterPath(review.getMovie().getPoster())
                .content(review.getContent())
                .score(review.getMovieScore())
                .reviewLike(review.getReviewLike())
                .isHide(review.isHide())
                .isSpoiler(review.isSpoiler())
                .commentCount(review.getReviewComments().size())
                .createdAt(review.getCreatedAt().format(dateTimeFormatter))
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminReviewDetailCommentsResponse> getMovieReviewsDetailComments(Long reviewId) {

        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보는 존재하지 않습니다. " + reviewId));

        List<ReviewComment> reviewComments = reviewCommentRepository.findCommentsWithUserByReviewId(reviewId);

        if (reviewComments.isEmpty()) {
            return Collections.emptyList();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

        return reviewComments.stream()
                .map(reviewComment -> AdminReviewDetailCommentsResponse.builder()
                        .commentId(reviewComment.getId())
                        .username(reviewComment.getUser().getNickname())
                        .userProfile(reviewComment.getUser().getProfileImage())
                        .content(reviewComment.getComment())
                        .createdAt(reviewComment.getCreatedAt().format(dateTimeFormatter))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminReviewDetailLikesResponse> getMovieReviewsDetailLikes(Long reviewId) {

        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보는 존재하지 않습니다. " + reviewId));

        List<ReviewLike> reviewLikes = reviewLikeRepository.findAllByReviewId(reviewId);

        if (reviewLikes.isEmpty()) {
            return Collections.emptyList();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

        return reviewLikes.stream()
                .map(reviewLike -> AdminReviewDetailLikesResponse.builder()
                        .likeId(reviewLike.getId())
                        .username(reviewLike.getUser().getNickname())
                        .userProfile(reviewLike.getUser().getProfileImage())
                        .createdAt(reviewLike.getCreatedAt().format(dateTimeFormatter))
                        .build())
                .toList();
    }

    @Transactional
    public AdminReviewHideResponse updateReviewHideStatus(Long reviewId, boolean hideStatus) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보는 존재하지 않습니다. " + reviewId));

        review.updateIsHide(hideStatus);

        return AdminReviewHideResponse.builder()
                .reviewId(review.getId())
                .isHide(review.isHide())
                .build();
    }

    @Transactional
    public AdminReviewSpoilerResponse updateReviewSpoilerStatus(Long reviewId, boolean spoilerStatus) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 정보는 존재하지 않습니다. " + reviewId));

        review.updateIsSpoiler(spoilerStatus);

        return AdminReviewSpoilerResponse.builder()
                .reviewId(review.getId())
                .isSpoiler(review.isSpoiler())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminMovieLikesResponse getMovieLikes(Long movieId, String dateOrder) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        List<MovieLike> movieLikes = movieLikeRepository.findAllByMovieId(movieId);

        movieLikes.sort(dateOrder.equals("latest") ?
                Comparator.comparing(MovieLike::getCreatedAt).reversed() :
                Comparator.comparing(MovieLike::getCreatedAt));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

        List<AdminReviewDetailLikesResponse> likesResponses = movieLikes.stream()
                .map(response -> AdminReviewDetailLikesResponse.builder()
                        .likeId(response.getId())
                        .username(response.getUser().getNickname())
                        .userProfile(response.getUser().getProfileImage())
                        .createdAt(response.getCreatedAt().format(dateTimeFormatter))
                        .build())
                .toList();

        return AdminMovieLikesResponse.builder()
                        .title(movie.getTitle())
                        .posterPath(movie.getPoster())
                        .likeAmount(movieLikes.size())
                        .movieLikeUsers(likesResponses)
                        .build();
    }

    private String getTrimmedContent(String content) {
        if (content.length() > 20) {
            return content.substring(0, 20) + "...";
        } else {
            return content;
        }
    }
}
