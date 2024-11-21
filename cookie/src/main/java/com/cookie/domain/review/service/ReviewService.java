package com.cookie.domain.review.service;

import com.cookie.domain.movie.dto.response.ReviewMovieResponse;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.request.ReviewCommentRequest;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.dto.response.ReviewCommentResponse;
import com.cookie.domain.review.dto.response.ReviewDetailResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.dto.request.UpdateReviewRequest;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewComment;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewCommentRepository;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.CommentUserResponse;
import com.cookie.domain.user.dto.response.ReviewUserResponse;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public void createReview(Long userId, CreateReviewRequest createReviewRequest, CopyOnWriteArrayList<SseEmitter> emitters) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        Long movieId = createReviewRequest.getMovieId();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));
        log.info("Retrieved movie: movieId = {}", movieId);

        if (reviewRepository.findByUserAndMovie(user, movie).isPresent()) {
            throw new IllegalArgumentException("해당 영화에 이미 리뷰를 등록했습니다.");
        }

        Review review = createReviewRequest.toEntity(user, movie);
        reviewRepository.save(review);
        log.info("Created review: userId = {}, movieId = {}", userId, movieId);

        sendReviewCreatedEvent(review, emitters);
    }

    private void sendReviewCreatedEvent(Review review, CopyOnWriteArrayList<SseEmitter> emitters) {
        ReviewResponse reviewResponse = ReviewResponse.fromReview(review);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("review-created")
                        .data(reviewResponse)); // ReviewResponse 전송
            } catch (Exception e) {
                log.error("Failed to send event to emitter: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }


    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequest updateReviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        review.update(updateReviewRequest.getContent(), updateReviewRequest.getMovieScore(), updateReviewRequest.getIsSpoiler());
        log.info("Updated review: reviewId = {}", reviewId);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewList() {
        List<Review> reviewList = reviewRepository.findAllWithMovieAndUser();
        log.info("Total reviews: {}", reviewList.size());

        return reviewList.stream()
                .map(ReviewResponse::fromReview)
                .toList();

    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        reviewRepository.delete(review);
        log.info("Deleted review: reviewId = {}", reviewId);
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        List<ReviewComment> reviewComments = reviewCommentRepository.findCommentsWithUserByReviewId(reviewId);

        List<ReviewCommentResponse> comments = reviewComments.stream()
                .map(comment -> new ReviewCommentResponse(
                        new CommentUserResponse(
                                comment.getUser().getNickname(),
                                comment.getUser().getProfileImage()),
                        comment.getCreatedAt(),
                        comment.getComment()))
                .toList();

        return new ReviewDetailResponse(
                review.getContent(),
                review.getMovieScore(),
                review.getReviewLike(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                new ReviewMovieResponse(review.getMovie().getTitle(), review.getMovie().getPoster()),
                new ReviewUserResponse(
                        review.getUser().getNickname(),
                        review.getUser().getProfileImage(),
                        review.getUser().getMainBadge() != null ? review.getUser().getMainBadge().getBadgeImage() : null),

                comments
        );

    }

    @Transactional
    public void addReviewLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        if (review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        ReviewLike existingLike = reviewLikeRepository.findByUserAndReview(user, review);
        if (existingLike != null) {
            reviewLikeRepository.delete(existingLike);
            review.decreaseLikeCount();
            log.info("Removed like from reviewId: {}", reviewId);
        } else {
            ReviewLike like = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(like);
            review.increaseLikeCount();
            log.info("Added like to reviewId: {}", reviewId);
        }
    }

    @Transactional
    public void createComment(Long reviewId, Long userId, ReviewCommentRequest reviewCommentRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        if (review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 리뷰에는 댓글을 작성할 수 없습니다.");
        }

        ReviewComment comment = ReviewComment.builder()
                .user(user)
                .review(review)
                .comment(reviewCommentRequest.getComment())
                .build();

        reviewCommentRepository.save(comment);
        log.info("Created comment for reviewId: {} by userId: {}", reviewId, userId);

    }

    @Transactional
    public void updateComment(Long commentId, ReviewCommentRequest reviewCommentRequest) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("not found commentId: " + commentId));
        log.info("Retrieved comment: commentId = {}", commentId);

        comment.update(reviewCommentRequest.getComment());
        log.info("Updated comment: commentId = {}", commentId);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("not found commentId: " + commentId));
        log.info("Retrieved comment: commentId = {}", commentId);

        reviewCommentRepository.delete(comment);
        log.info("Deleted comment: commentId = {}", commentId);
    }

}

