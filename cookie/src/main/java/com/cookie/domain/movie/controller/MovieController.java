package com.cookie.domain.movie.controller;


import com.cookie.domain.movie.dto.MovieResponse;
import com.cookie.domain.movie.service.MovieLikeService;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieLikeController {

    private final MovieLikeService movieLikeService;

}
