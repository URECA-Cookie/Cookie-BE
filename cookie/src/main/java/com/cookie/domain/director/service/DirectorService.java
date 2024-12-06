package com.cookie.domain.director.service;

import com.cookie.domain.director.dto.response.DirectorDetailResponse;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.director.repository.DirectorRepository;
import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.dto.response.PersonDetailMovieInfo;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import com.cookie.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final ReviewRepository reviewRepository;
    private final MovieLikeRepository movieLikeRepository;

    public DirectorDetailResponse getDirectorDetails(Long directorId) {
        // 1. 감독 정보 가져오기
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new IllegalArgumentException("Director not found with id: " + directorId));

        // 2. 디렉팅한 영화 정보 가져오기
        List<Director> movieDirectors = directorRepository.findAllMoviesByDirectorId(directorId);

        // 3. ActorMovie 리스트 생성
        // 3. DirectorMovie 리스트 생성
        List<MovieSimpleResponse> directorMovieList = movieDirectors.stream()
                .flatMap(movieDirector -> movieDirector.getMovies().stream())
                .map(movie -> MovieSimpleResponse.builder()
                        .id(movie.getId()) // Movie ID
                        .title(movie.getTitle()) // 제목
                        .poster(movie.getPoster()) // 포스터
                        .releasedAt(movie.getReleasedAt()) // LocalDateTime -> LocalDate 변환
                        .country(movie.getCountry().getName()) // 제작 국가 이름
                        .score(movie.getScore())
                        .likes(movieLikeRepository.countByMovieId(movie.getId())) // 좋아요 수
                        .reviews(reviewRepository.countByMovieId(movie.getId())) // 리뷰 개수
                        .build()
                )
                .collect(Collectors.toList());


        // 4. PersonDetailMovieInfo 생성 및 반환
        return DirectorDetailResponse.builder()
                .name(director.getName())
                .profileImage(director.getProfileImage())
                .directorMovieList(directorMovieList)
                .build();
    }
}
