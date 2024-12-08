package com.cookie.domain.review.dto.response;


import com.cookie.domain.user.dto.response.CommentUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentResponse {
    private Long commentId;
    private CommentUserResponse user;
    private LocalDateTime createdAt;
    private String comment;
  
}
