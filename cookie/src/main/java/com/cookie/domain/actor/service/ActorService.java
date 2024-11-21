package com.cookie.domain.actor.service;

import com.cookie.domain.actor.dto.response.ActorDetailResponse;
import com.cookie.domain.actor.entity.Actor;
import com.cookie.domain.actor.repository.ActorRepository;
import com.cookie.domain.movie.dto.response.PersonDetailMovieInfo;
import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.repository.MovieActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieActorRepository movieActorRepository;

    public ActorDetailResponse getActorDetails(Long actorId) {
        // 1. 배우 정보 가져오기
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found with id: " + actorId));

        // 2. 배우가 출연한 영화 정보 가져오기
        List<MovieActor> movieActors = movieActorRepository.findAllMoviesByActorId(actorId);

        // 3. ActorMovie 리스트 생성
        List<PersonDetailMovieInfo> actorMovieList = movieActors.stream()
                .map(movieActor -> {
                    var movie = movieActor.getMovie();
                    return PersonDetailMovieInfo.builder()
                            .title(movie.getTitle())
                            .poster(movie.getPoster())
                            .released(movie.getReleasedAt().toLocalDate().toString()) // LocalDateTime -> LocalDate 변환
                            .build();
                })
                .collect(Collectors.toList());

        // 4. ActorDetailResponse 생성 및 반환
        return ActorDetailResponse.builder()
                .name(actor.getName())
                .profileImage(actor.getProfileImage())
                .actorMovieList(actorMovieList)
                .build();
    }
}
