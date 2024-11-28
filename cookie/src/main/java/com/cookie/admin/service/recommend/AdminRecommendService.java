package com.cookie.admin.service.recommend;

import com.cookie.admin.dto.response.RecommendResponse;
import com.cookie.admin.entity.AdminMovieRecommend;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.RecommendRepository;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminRecommendService {

    private final RecommendRepository recommendRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<RecommendResponse> getRecommendMovies() {
        List<AdminMovieRecommend> recommends = recommendRepository.findAll();

        return recommends.stream()
                .map(data -> RecommendResponse.builder()
                        .id(data.getId())
                        .movieId(data.getMovie().getId())
                        .title(data.getMovie().getTitle())
                        .posterPath(data.getMovie().getPoster())
                        .build())
                .toList();
    }

    @Transactional
    public void recommendMovies(Set<Long> movieIds) {

        if (movieIds.isEmpty()) {
            recommendRepository.deleteAll();
        } else {
            List<Movie> movies = movieRepository.findAllById(movieIds);

            if (movies.isEmpty()) {
                throw new MovieNotFoundException("존재하지 않는 영화 정보들 입니다.");
            }

            if (movies.size() != movieIds.size()) {
                throw new MovieNotFoundException("유효하지 않은 영화 id가 포함되어 있습니다.");
            }

            List<AdminMovieRecommend> adminMovieRecommends = movies.stream()
                    .map(movie -> AdminMovieRecommend.builder()
                            .movie(movie)
                            .build()
                    ).toList();

            recommendRepository.deleteAll();
            recommendRepository.saveAll(adminMovieRecommends);
        }
    }
}
