package com.cookie.domain.review.service;


import com.cookie.domain.movie.dto.response.ReviewMovieResponse;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.notification.entity.FcmToken;
import com.cookie.domain.notification.service.NotificationService;
import com.cookie.domain.review.dto.request.ReviewCommentRequest;
import com.cookie.domain.review.dto.request.CreateReviewRequest;
import com.cookie.domain.review.dto.response.PushNotification;
import com.cookie.domain.review.dto.response.ReviewCommentResponse;
import com.cookie.domain.review.dto.response.ReviewDetailResponse;
import com.cookie.domain.review.dto.response.ReviewListResponse;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.dto.response.*;
import com.cookie.domain.review.dto.request.UpdateReviewRequest;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewComment;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.review.repository.ReviewCommentRepository;
import com.cookie.domain.review.repository.ReviewLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.CommentUserResponse;
import com.cookie.domain.user.dto.response.ReviewUserResponse;
import com.cookie.domain.user.entity.DailyGenreScore;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.ActionType;
import com.cookie.domain.user.repository.DailyGenreScoreRepository;
import com.cookie.domain.user.repository.UserRepository;
import com.cookie.domain.user.service.DailyGenreScoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final DailyGenreScoreRepository dailyGenreScoreRepository;
    private final DailyGenreScoreService dailyGenreScoreService;
    private final NotificationService notificationService;

    @Transactional
    public void createReview(Long userId, CreateReviewRequest createReviewRequest, CopyOnWriteArrayList<SseEmitter> reviewEmitters, CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters) {
        long startTime = System.currentTimeMillis();
        log.info("Start createReview");

        long stepTime = System.currentTimeMillis();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}, Time Taken: {} ms", userId, System.currentTimeMillis() - stepTime);

        stepTime = System.currentTimeMillis();
        Long movieId = createReviewRequest.getMovieId();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));
        log.info("Retrieved movie: movieId = {}, Time Taken: {} ms", movieId, System.currentTimeMillis() - stepTime);

        stepTime = System.currentTimeMillis();
        if (reviewRepository.findByUserAndMovie(user, movie).isPresent()) {
            throw new IllegalArgumentException("해당 영화에 이미 리뷰를 등록했습니다.");
        }
        log.info("Checked for existing review, Time Taken: {} ms", System.currentTimeMillis() - stepTime);

        // 영화 평점이 0.0일 경우 평점 반영
        if (movie.getScore() == 0.0) {
            stepTime = System.currentTimeMillis();
            movie.updateScore((double) createReviewRequest.getMovieScore());
            log.info("Updated movie score, Time Taken: {} ms", System.currentTimeMillis() - stepTime);
        }

        stepTime = System.currentTimeMillis();
        List<String> genres = movie.getMovieCategories().stream()
                .filter(mc -> "장르".equals(mc.getCategory().getMainCategory()))
                .map(mc -> mc.getCategory().getSubCategory())
                .toList();
        log.info("Extracted genres, Time Taken: {} ms", System.currentTimeMillis() - stepTime);

        stepTime = System.currentTimeMillis();
        genres.forEach(genre -> dailyGenreScoreService.saveScore(user, genre, 7, ActionType.MOVIE_LIKE));
        log.info("Saved daily genre scores, Time Taken: {} ms", System.currentTimeMillis() - stepTime);

        stepTime = System.currentTimeMillis();
        Review review = createReviewRequest.toEntity(user, movie);
        Review savedReview = reviewRepository.save(review);
        log.info("Saved review: userId = {}, movieId = {}, Time Taken: {} ms", userId, movieId, System.currentTimeMillis() - stepTime);

        stepTime = System.currentTimeMillis();
        List<String> enGenres = movie.getMovieCategories().stream()
                .filter(mc -> "장르".equals(mc.getCategory().getMainCategory()))
                .map(mc -> mc.getCategory().getSubCategoryEn())
                .toList();
        log.info("Extracted genres (EN), Time Taken: {} ms", System.currentTimeMillis() - stepTime);

        for (String genre : enGenres) {
            stepTime = System.currentTimeMillis();
            List<String> userTokens = userRepository.findTokensByGenreAndExcludeUser(genre, userId);
            log.info("Fetched user tokens for genre '{}', Time Taken: {} ms", genre, System.currentTimeMillis() - stepTime);

            stepTime = System.currentTimeMillis();
            List<String> excludedTokens = user.getFcmTokens().stream()
                    .map(FcmToken::getToken)
                    .toList();
            log.info("Extracted excluded tokens, Time Taken: {} ms", System.currentTimeMillis() - stepTime);

            stepTime = System.currentTimeMillis();
            String title = String.format("%s님 새로운 리뷰가 등록되었습니다!", user.getId());
            String body = String.format("%s님이 %s 영화에 리뷰를 남겼습니다.", user.getNickname(), movie.getTitle());
            notificationService.sendPushNotificationToUsers(userTokens, title, body, excludedTokens);
            log.info("Sent push notification for genre '{}', Time Taken: {} ms", genre, System.currentTimeMillis() - stepTime);
        }

        stepTime = System.currentTimeMillis();
        sendReviewCreatedEvent(savedReview, reviewEmitters);
        log.info("Sent review created event, Time Taken: {} ms", System.currentTimeMillis() - stepTime);

        log.info("End createReview, Total Time Taken: {} ms", System.currentTimeMillis() - startTime);
    }


    @Async
    public void sendReviewCreatedEvent(Review review, CopyOnWriteArrayList<SseEmitter> reviewEmitters) {
        ReviewResponse reviewResponse = ReviewResponse.fromReview(review, false, Long.valueOf(review.getReviewComments().size()));

        for (SseEmitter emitter : reviewEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("review-created")
                        .data(reviewResponse)); // ReviewResponse 전송
            } catch (Exception e) {
                log.error("Failed to send event to emitter: {}", e.getMessage());
                reviewEmitters.remove(emitter);
            }
        }
    }

//    @Async
//    public void sendPushNotification(Long userId, Movie movie, Review review, CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters) {
//        log.info("movie title: {}", movie.getTitle());
//
//        List<String> genres = movieRepository.findGenresByMovieId(movie.getId());
//        log.info("영화 [{}]의 장르 정보: {}", movie.getTitle(), genres);
//
//        List<User> genreFans = userRepository.findUsersByFavoriteGenresInAndExcludeUserId(genres, userId);
//        log.info("장르를 좋아하는 유저 {}명", genreFans.size());
//
//        PushNotification pushNotification = new PushNotification(review.getMovie().getId(), review.getMovie().getTitle(), review.getUser().getNickname());
//
//        sendNotificationToUser(genreFans, pushNotification, pushNotificationEmitters);
//    }
//    private void sendNotificationToUser(List<User> genreFans, PushNotification pushNotification, CopyOnWriteArrayList<SseEmitter> pushNotificationEmitters) {
//        for (User fan : genreFans) {
//            if (fan.isPushEnabled()) { // pushEnabled 가 true 인 경우에만 알림 전송
//                try {
//                    String notificationMessage = String.format("[%s]님 새로운 리뷰가 등록되었습니다! [%s]", fan.getNickname(), pushNotification.getMovieTitle());
//                    log.info("푸시 알림 전송 성공: {}", notificationMessage);
//
//                    for (SseEmitter emitter : pushNotificationEmitters) {
//                        try {
//                            emitter.send(SseEmitter.event()
//                                    .name("push-notification")
//                                    .data(pushNotification));
//                        } catch (Exception e) {
//                            log.error("Failed to send event to emitter for user [{}]: {}", fan.getId(), e.getMessage());
//                            pushNotificationEmitters.remove(emitter);
//                        }
//                    }
//                } catch (Exception e) {
//                    log.error("Failed to send push notification to user: {}", fan.getId(), e);
//                }
//            } else {
//                log.info("유저 [{}]는 푸시 알림을 비활성화 했습니다.", fan.getId());
//            }
//        }
//    }


    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequest updateReviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        review.update(updateReviewRequest.getContent(), updateReviewRequest.getMovieScore(), updateReviewRequest.getIsSpoiler());
        log.info("Updated review: reviewId = {}", reviewId);
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviewList(Long userId, Pageable pageable) {
        Page<Review> reviewList = reviewRepository.findAllWithMovieAndUser(pageable);
        log.info("Total reviews: {}", reviewList.getTotalElements());

        List<ReviewResponse> reviewResponses = reviewList.stream()
                .map(review -> {
                    boolean likedByUser = userId != null &&
                            review.getReviewLikes().stream()
                                    .anyMatch(like -> like.getUser().getId().equals(userId));
                    return ReviewResponse.fromReview(review, likedByUser,Long.valueOf(review.getReviewComments().size()));
                })
                .toList();

        return new ReviewListResponse(
                reviewResponses,
                reviewList.getTotalElements(),
                reviewList.getTotalPages()
        );

    }

    @Transactional(readOnly = true)
    public ReviewListResponse getSpoilerReviewList(Long userId, Pageable pageable) {
        Page<Review> reviewList = reviewRepository.findAllWithMovieAndUserWithSpoilers(pageable);
        log.info("Total reviews: {}", reviewList.getTotalElements());

        List<ReviewResponse> reviewResponses = reviewList.stream()
                .map(review -> {
                    boolean likedByUser = userId != null &&
                            review.getReviewLikes().stream()
                                    .anyMatch(like -> like.getUser().getId().equals(userId));
                    return ReviewResponse.fromReview(review, likedByUser, Long.valueOf(review.getReviewComments().size()));
                })
                .toList();

        return new ReviewListResponse(
                reviewResponses,
                reviewList.getTotalElements(),
                reviewList.getTotalPages()
        );
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
    public ReviewDetailResponse getReviewDetail(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        List<ReviewComment> reviewComments = reviewCommentRepository.findCommentsWithUserByReviewId(reviewId);

        List<ReviewCommentResponse> comments = reviewComments.stream()
                .map(comment -> new ReviewCommentResponse(
                        comment.getId(),
                        new CommentUserResponse(
                                comment.getUser().getId(),
                                comment.getUser().getNickname(),
                                comment.getUser().getProfileImage()),
                        comment.getCreatedAt(),
                        comment.getComment()))
                .toList();

        boolean likedByUser = userId != null && reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);

        return new ReviewDetailResponse(
                review.getContent(),
                review.getMovieScore(),
                review.getReviewLike(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                new ReviewMovieResponse(review.getMovie().getTitle(), review.getMovie().getPoster()),
                new ReviewUserResponse(
                        review.getUser().getId(),
                        review.getUser().getNickname(),
                        review.getUser().getProfileImage(),
                        review.getUser().getMainBadge() != null ? review.getUser().getMainBadge().getBadgeImage() : null),

                comments,
                likedByUser
        );

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

    @Transactional(readOnly = true)
    public ReviewPagenationResponse getLikedReviewsByUserId(Long userId, int page, int size) {
        // 페이징 요청 생성
        Pageable pageable = PageRequest.of(page, size);

        // ReviewLike 엔티티를 페이징 처리하여 조회
        Page<ReviewLike> likedReviewsPage = reviewLikeRepository.findAllByUserIdWithReviews(userId, pageable);

        // ReviewLike -> ReviewResponse 변환
        List<ReviewResponse> reviews = likedReviewsPage.getContent().stream()
                .map(reviewLike -> {
                    Review review = reviewLike.getReview();
                    return ReviewResponse.fromReview(review, true,Long.valueOf(review.getReviewComments().size()));
                })
                .toList();

        // ReviewPagenationResponse 생성
        return ReviewPagenationResponse.builder()
                .currentPage(page)
                .reviews(reviews)
                .totalPages(likedReviewsPage.getTotalPages())
                .build();
    }

}

