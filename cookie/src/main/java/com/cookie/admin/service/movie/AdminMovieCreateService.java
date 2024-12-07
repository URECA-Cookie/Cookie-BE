package com.cookie.admin.service.movie;

import com.cookie.admin.dto.response.AdminMovieBaseAddResponse;
import com.cookie.admin.dto.response.AdminMovieTMDBDetailResponse;
import com.cookie.admin.dto.response.tmdb.TMDBCasts;
import com.cookie.admin.dto.response.tmdb.TMDBMovieSearchResponse;
import com.cookie.admin.exception.MovieAlreadyExistsException;
import com.cookie.domain.category.repository.CategoryRepository;
import com.cookie.admin.repository.CountryRepository;
import com.cookie.domain.actor.entity.Actor;
import com.cookie.domain.actor.repository.ActorRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.country.entity.Country;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.director.repository.DirectorRepository;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.entity.MovieCategory;
import com.cookie.domain.movie.entity.MovieImage;
import com.cookie.domain.movie.repository.MovieActorRepository;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieImageRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMovieCreateService {

    @Value("${tmdb.api.key}")
    private String apiKey;
    private final WebClient webClient;

    private final TMDBService TMDBService;

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final MovieImageRepository movieImageRepository;
    private final CountryRepository countryRepository;
    private final CategoryRepository categoryRepository;

    private final MovieActorRepository movieActorRepository;
    private final MovieCategoryRepository movieCategoryRepository;

    @Transactional
    public AdminMovieBaseAddResponse defaultMoviesAdd() {
        long movieAddCount = 0;

        for (int year = 2024; year >= 2019; year--) {
            for (int page = 1; page <= 2; page++) {
                String urlDiscover = createDiscoverUrl(year, page);
                System.out.println("urlDiscover = " + urlDiscover);

                TMDBMovieSearchResponse response = webClient.get()
                        .uri(urlDiscover)
                        .header("accept", "application/json")
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                                Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                        )
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                                Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                        )
                        .bodyToMono(TMDBMovieSearchResponse.class)
                        .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                        .block();

                if (response == null || response.getResults().isEmpty()) {
                    continue;
                }

                List<Movie> movies = response.getResults().stream()
                        .map(movie -> {
                            try {
                                AdminMovieTMDBDetailResponse movieDetail = TMDBService.getMovieInfoById(movie.getId());
                                return createMovie(movieDetail);
                            } catch (MovieAlreadyExistsException e) {
                                System.out.println("이미 등록된 영화입니다: " + movie.getId());
                                return null;
                            } catch (Exception e) {
                                System.out.println("영화 저장 중 오류 발생: " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull) // null 제거
                        .collect(Collectors.toList());

                if (!movies.isEmpty()) {
                    movieRepository.saveAll(movies);
                    movieAddCount += movies.size();
                }

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("요청 지연 중 오류 발생: " + e.getMessage());
                }
            }
        }

        return AdminMovieBaseAddResponse.builder()
                .movieAddCount(movieAddCount)
                .build();
    }


    @Transactional
    public Movie createMovie(AdminMovieTMDBDetailResponse movie) {

        Optional<Movie> findMovie = movieRepository.findMovieByTMDBMovieId(movie.getMovieId());

        if (findMovie.isPresent()) {
            throw new MovieAlreadyExistsException("이미 해당 영화 정보가 있습니다");
        }

        Director directorData = createDirector(movie);
        Country countryData = createCountry(movie);
        Movie movieData = createMovieData(movie, directorData, countryData);
        movieRepository.save(movieData);

        List<MovieImage> movieImageDates = createMovieImages(movie, movieData);
        movieImageRepository.saveAll(movieImageDates);

        List<Actor> actorDates = createActors(movie);
        List<MovieActor> movieActors = createMovieActors(movieData, actorDates);
        movieActorRepository.saveAll(movieActors);

        List<Category> categories = createCategories(movie);
        List<MovieCategory> movieCategoryDates = createMovieCategories(movieData, categories);
        movieCategoryRepository.saveAll(movieCategoryDates);

        return movieData;
    }

    private Movie createMovieData(AdminMovieTMDBDetailResponse movie, Director director, Country country) {

        return Movie.builder()
                .director(director)
                .country(country)
                .TMDBMovieId(movie.getMovieId())
                .title(movie.getTitle())
                .poster(movie.getPosterPath())
                .youtubeUrl(movie.getYoutube())
                .plot(movie.getPlot())
                .releasedAt(movie.getReleaseDate())
                .runtime(movie.getRuntime())
                .certification(movie.getCertification())
                .movieLikes(0L)
                .build();
    }

    private List<Actor> createActors(AdminMovieTMDBDetailResponse movie) {
        List<Long> actorIds = movie.getActors().stream().map(TMDBCasts::getTmdbCasterId).toList();
        List<Actor> existingActors = actorRepository.findAllByTmdbCasterIdIn(actorIds);

        List<Actor> newActors = movie.getActors().stream()
                .filter(data -> existingActors.stream().noneMatch(actor -> actor.getTmdbCasterId().equals(data.getTmdbCasterId())))
                .map(data -> actorRepository.save(
                        Actor.builder()
                                .tmdbCasterId(data.getTmdbCasterId())
                                .name(data.getName())
                                .profileImage(data.getProfilePath())
                                .build()))
                .toList();

        List<Actor> allActors = new ArrayList<>();
        allActors.addAll(existingActors);
        allActors.addAll(newActors);

        return allActors;
    }

    private List<MovieActor> createMovieActors(Movie movieData, List<Actor> actorDates) {

        return actorDates.stream()
                .map(actor -> MovieActor.builder()
                        .movie(movieData)
                        .actor(actor)
                        .build())
                .toList();
    }

    private Director createDirector(AdminMovieTMDBDetailResponse movie) {

        return directorRepository.findByTMDBCasterId(movie.getDirector().getTmdbCasterId())
                .orElseGet(() -> {
                    Director director = Director.builder()
                            .tmdbCasterId(movie.getDirector().getTmdbCasterId())
                            .name(movie.getDirector().getName())
                            .profileImage(movie.getDirector().getProfilePath())
                            .build();
                    return directorRepository.save(director);
                });
    }

    private List<MovieImage> createMovieImages(AdminMovieTMDBDetailResponse movie, Movie movieData) {

        return movie.getStillCuts().stream()
                .map(url -> MovieImage.builder()
                        .movie(movieData)
                        .url(url)
                        .build())
                .collect(Collectors.toList());
    }

    private Country createCountry(AdminMovieTMDBDetailResponse movie) {

        return countryRepository.findByCountry(movie.getCountry())
                .orElseGet(() -> countryRepository.findByCountry("N/A")
                        .orElseThrow(() -> new EntityNotFoundException("해당 영화 국가를 찾지 못했습니다")));
    }

    private List<Category> createCategories(AdminMovieTMDBDetailResponse movie) {
        List<String> categoryNames = movie.getCategories();
        List<Category> categories = categoryRepository.findAllByNameIn(categoryNames);

        return categoryNames.stream()
                .map(categoryName -> categories.stream()
                        .filter(category -> category.getSubCategory().equals(categoryName))
                        .findFirst()
                        .orElseGet(() -> categoryRepository.findCategoryByName("N/A")
                                .orElseThrow(() -> new EntityNotFoundException("해당 영화 카테고리를 찾지 못했습니다"))))
                .toList();
    }

    private List<MovieCategory> createMovieCategories(Movie movieData, List<Category> categories) {

        return categories.stream()
                .map(category -> MovieCategory.builder()
                        .movie(movieData)
                        .category(category)
                        .build())
                .toList();
    }

    private String createDiscoverUrl(int year, int page) {

        String baseUrl = "https://api.themoviedb.org/3/discover/movie?api_key=";
        String query = "&primary_release_year=" + year + "&sort_by=vote_count.desc&language=ko-KR&page=" + page;
        return baseUrl + apiKey + query;
    }
}
