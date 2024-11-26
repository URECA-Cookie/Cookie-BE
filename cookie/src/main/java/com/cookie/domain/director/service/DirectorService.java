package com.cookie.domain.director.service;

import com.cookie.domain.director.dto.response.DirectorDetailResponse;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.director.repository.DirectorRepository;
import com.cookie.domain.movie.dto.response.PersonDetailMovieInfo;
import com.cookie.domain.movie.entity.MovieDirector;
import com.cookie.domain.movie.repository.MovieDirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final MovieDirectorRepository movieDirectorRepository;

    public DirectorDetailResponse getDirectorDetails(Long directorId) {
        // 1. 감독 정보 가져오기
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new IllegalArgumentException("Director not found with id: " + directorId));

        // 2. 디렉팅한 영화 정보 가져오기
        List<MovieDirector> movieDirectors = movieDirectorRepository.findAllMoviesByDirectorId(directorId);

        // 3. ActorMovie 리스트 생성
        List<PersonDetailMovieInfo> directorMovieList = movieDirectors.stream()
                .map(movieDirector -> {
                    var movie = movieDirector.getMovie();
                    return PersonDetailMovieInfo.builder()
                            .title(movie.getTitle())
                            .poster(movie.getPoster())
                            .released(movie.getReleasedAt())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. PersonDetailMovieInfo 생성 및 반환
        return DirectorDetailResponse.builder()
                .name(director.getName())
                .profileImage(director.getProfileImage())
                .directorMovieList(directorMovieList)
                .build();
    }
}
