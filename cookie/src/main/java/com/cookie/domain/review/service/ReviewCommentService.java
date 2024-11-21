package com.cookie.domain.review.service;

import com.cookie.domain.review.dto.response.ReviewCommentResponse;
import com.cookie.domain.review.entity.ReviewComment;
import com.cookie.domain.review.repository.ReviewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;

    public List<ReviewCommentResponse> getCommentsByUserId(Long userId) {
        List<ReviewComment> comments = reviewCommentRepository.findAllByUserIdWithReviewAndMovie(userId);

        return comments.stream()
                .map(comment -> ReviewCommentResponse.builder()
                        .movieTitle(comment.getReview().getMovie().getTitle()) // 영화 제목
                        .reviewContent(comment.getReview().getContent())       // 리뷰 내용
                        .commentContent(comment.getComment())                  // 댓글 내용
                        .build())
                .collect(Collectors.toList());
    }
}
