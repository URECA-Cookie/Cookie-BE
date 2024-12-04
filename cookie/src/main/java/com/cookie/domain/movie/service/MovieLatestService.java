package com.cookie.domain.movie.service;

import com.cookie.admin.dto.response.AdminMovieTMDBDetailResponse;
import com.cookie.admin.service.movie.AdminMovieCreateService;
import com.cookie.admin.service.movie.TMDBService;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieMonthRanking;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.movie.repository.MovieMonthRankingRepository;
import com.cookie.domain.search.dto.response.MovieDateTimeTMDB;
import com.cookie.domain.search.dto.response.MovieDateTimeTMDBResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MovieLatestService {

    @Value("${tmdb.api.key}")
    private String apiKey;
    private final WebClient webClient;

    private final MovieMonthRankingRepository movieMonthRankingRepository;
    private final MovieRepository movieRepository;
    private final AdminMovieCreateService adminMovieCreateService;
    private final TMDBService tmdbService;

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public void createDailyMovies() {

        List<MovieDateTimeTMDB> dailyCreateMovies = fetchMoviesDailyFromTMDB();

        if (dailyCreateMovies.isEmpty()) {
            return;
        }

        dailyCreateMovies.sort(Comparator.comparing(MovieDateTimeTMDB::getPopularity).reversed());

        List<Long> createMovieIds = dailyCreateMovies.stream().map(MovieDateTimeTMDB::getId).toList();

        if (createMovieIds.size() > 3) {
            createMovieIds = new ArrayList<>(createMovieIds.subList(0, 3));
        }

        createMovies(createMovieIds);
    }

    @Transactional
    public void createMoviesMonthRanking() {

        List<MovieDateTimeTMDB> movieDateTimeTMDBS = fetchMoviesWeekFromTMDB();

        List<Long> createMovieIds = fetchMissingMovieIds(movieDateTimeTMDBS);
        List<Movie> createdMovies = createMovies(createMovieIds);

        List<Movie> existingMovies = fetchExistMovieIds(movieDateTimeTMDBS);

        List<Movie> addMovieWeekDates = Stream.concat(createdMovies.stream(), existingMovies.stream())
                .distinct()
                .collect(Collectors.toList());

        saveWeekMovies(addMovieWeekDates, movieDateTimeTMDBS);
    }

    private List<MovieDateTimeTMDB> fetchMoviesDailyFromTMDB() {

        LocalDate today = LocalDate.now();
        String date = today.format(DATE_TIME_FORMATTER);

        int maxPage = 3;

        List<CompletableFuture<List<MovieDateTimeTMDB>>> futures = new ArrayList<>();

        for (int page = 1; page <= maxPage; page++) {
            String url = createMovieDailyUrl(date, page);
            futures.add(CompletableFuture.supplyAsync(() -> getMovieDailyDates(url)));
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(movie -> movie.getOriginalLanguage().equals("ko"))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<MovieDateTimeTMDB> getMovieDailyDates(String url) {
        MovieDateTimeTMDBResponse dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(MovieDateTimeTMDBResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null) {
            return Collections.emptyList();
        }

        return dates.getResults().stream()
                .map(data -> MovieDateTimeTMDB.builder()
                        .title(data.getTitle())
                        .id(data.getId())
                        .originalLanguage(data.getOriginalLanguage())
                        .popularity(data.getPopularity())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Movie> createMovies(List<Long> createMovieIds) {

        List<Movie> movies = new ArrayList<>();

        for (Long createMovieId : createMovieIds) {
            AdminMovieTMDBDetailResponse movieData = tmdbService.getMovieInfoById(createMovieId);
            Movie movie = adminMovieCreateService.createMovie(movieData);
            movies.add(movie);
        }

        return movies;
    }

    private void saveWeekMovies(List<Movie> addMovieWeekDates, List<MovieDateTimeTMDB> movieDateTimeTMDBS) {
        Map<Long, Double> moviePopularityMap = movieDateTimeTMDBS.stream()
                .collect(Collectors.toMap(
                        MovieDateTimeTMDB::getId,
                        MovieDateTimeTMDB::getPopularity,
                        (existing, replacement) -> existing
                ));

        addMovieWeekDates.sort(Comparator.comparing(
                movie -> moviePopularityMap.get(movie.getTMDBMovieId()),
                Comparator.reverseOrder()
        ));

        List<MovieMonthRanking> movieMonthRankings = new ArrayList<>();

        int rank = 1;

        for (Movie movie : addMovieWeekDates) {
            MovieMonthRanking movieMonthRanking = MovieMonthRanking.builder()
                    .ranking(rank++)
                    .movie(movie)
                    .build();

            movieMonthRankings.add(movieMonthRanking);
        }

        movieMonthRankingRepository.deleteAll();
        movieMonthRankingRepository.saveAll(movieMonthRankings);
    }

    private List<Movie> fetchExistMovieIds(List<MovieDateTimeTMDB> movieDateTimeTMDBS) {
        return movieRepository.findAllByTMDBMovieIds(
                movieDateTimeTMDBS.stream().map(MovieDateTimeTMDB::getId).collect(Collectors.toSet()));
    }

    private List<Long> fetchMissingMovieIds(List<MovieDateTimeTMDB> movieDateTimeTMDBS) {

        Set<Long> existingMovieIds = movieRepository.findAllByTMDBMovieIds(
                        movieDateTimeTMDBS.stream().map(MovieDateTimeTMDB::getId).collect(Collectors.toSet()))
                .stream().map(Movie::getTMDBMovieId).collect(Collectors.toSet());

        return movieDateTimeTMDBS.stream()
                .map(MovieDateTimeTMDB::getId)
                .filter(id -> !existingMovieIds.contains(id))
                .collect(Collectors.toList());
    }

    private List<MovieDateTimeTMDB> fetchMoviesWeekFromTMDB() {

        LocalDate today = LocalDate.now();
        LocalDate oneWeeksAgo = today.minusWeeks(1);
        LocalDate fourWeeksAgo = today.minusWeeks(5);

        String startDate = fourWeeksAgo.format(DATE_TIME_FORMATTER);
        String endDate = oneWeeksAgo.format(DATE_TIME_FORMATTER);

        String koreanPattern = ".*[가-힣]+.*";
        Pattern pattern = Pattern.compile(koreanPattern);

        int maxPage = 5;

        List<CompletableFuture<List<MovieDateTimeTMDB>>> futures = new ArrayList<>();

        for (int page = 1; page <= maxPage; page++) {
            String url = createMovieWeekUrl(startDate, endDate, page);
            futures.add(CompletableFuture.supplyAsync(() -> getMovieWeekDates(url)));
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(movie -> pattern.matcher(movie.getTitle()).matches())
                .limit(10)
                .collect(Collectors.toList());
    }


    private List<MovieDateTimeTMDB> getMovieWeekDates(String url) {
        MovieDateTimeTMDBResponse dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(MovieDateTimeTMDBResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null) {
            return Collections.emptyList();
        }

        return dates.getResults().stream()
                .map(data -> MovieDateTimeTMDB.builder()
                        .title(data.getTitle())
                        .id(data.getId())
                        .originalLanguage(data.getOriginalLanguage())
                        .popularity(data.getPopularity())
                        .build())
                .collect(Collectors.toList());
    }


    private String createMovieDailyUrl(String today, int page) {
        String baseUrl = "https://api.themoviedb.org/3/discover/movie?api_key=";
        String dateQuery1  = "&primary_release_date.gte=";
        String dateQuery2 = "&primary_release_date.lte=";
        return baseUrl + apiKey + dateQuery1 + today + dateQuery2 + today + "&language=ko-KR&page=" + page;
    }

    private String createMovieWeekUrl(String startDate, String endDate, int page) {
        String baseUrl = "https://api.themoviedb.org/3/discover/movie?api_key=";
        String param  = "&language=ko-KR&sort_by=popularity.desc&primary_release_date.gte=";
        String dateQuery = "&primary_release_date.lte=";
        return baseUrl + apiKey + param + startDate + dateQuery + endDate + "&page=" + page;
    }

}
