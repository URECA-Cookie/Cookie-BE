package com.cookie.admin.service.movie;

import com.cookie.admin.dto.response.AdminMovieDetailResponse;
import com.cookie.admin.dto.response.AdminSearchResponse;
import com.cookie.admin.dto.response.tmdb.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TMDBService {

    @Value("${tmdb.api.key}")
    private String apiKey;
    private final String imageUrl = "https://image.tmdb.org/t/p/w500/";
    private final WebClient webClient;

    @Transactional(readOnly = true)
    public List<AdminSearchResponse> getMoviesByName(String movieName) {
        String searchUlr = createSearchUrl(movieName);

        TMDBMovieSearchResponse TMDBDates = webClient.get()
                .uri(searchUlr)
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

        if (TMDBDates == null || TMDBDates.getResults().isEmpty()) {
            return Collections.emptyList();
        }

        return TMDBDates.getResults().stream()
                .sorted(Comparator.comparing(TMDBMovieResponse::getVoteCount).reversed())
                .map(data -> AdminSearchResponse.builder()
                        .movieId(data.getId())
                        .title(data.getTitle())
                        .posterPath(imageUrl + data.getPosterPath())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminMovieDetailResponse getMovieInfoById(Long movieId) {
        String movieInfoUrl = createMovieDetailUrl(movieId);
        String creditsUrl = createMovieAboutUrl(movieId, "credits");
        String imagesUrl = createMovieImagesUrl(movieId);
        String videoUrl = createMovieAboutUrl(movieId, "videos");
        String certificationUrl = createMovieCertificationUrl(movieId);

        CompletableFuture<TMDBMovieDetailResponse> detailFuture =
                CompletableFuture.supplyAsync(() -> getMovieDetail(movieInfoUrl));

        CompletableFuture<List<TMDBCasts>> actorsFuture =
                CompletableFuture.supplyAsync(() -> getMovieActors(creditsUrl));

        CompletableFuture<Optional<TMDBCasts>> directorFuture =
                CompletableFuture.supplyAsync(() -> getMovieDirector(creditsUrl));

        CompletableFuture<List<String>> imagesFuture =
                CompletableFuture.supplyAsync(() -> getMovieImages(imagesUrl));

        CompletableFuture<Optional<String>> videoFuture =
                CompletableFuture.supplyAsync(() -> getMovieVideos(videoUrl));

        CompletableFuture<String> certificationFuture =
                CompletableFuture.supplyAsync(() -> getCertification(certificationUrl));

        CompletableFuture.allOf(detailFuture, actorsFuture, directorFuture, imagesFuture, videoFuture, certificationFuture).join();

        TMDBMovieDetailResponse detail = detailFuture.join();
        List<TMDBCasts> actors = actorsFuture.join();
        Optional<TMDBCasts> director = directorFuture.join();
        List<String> images = imagesFuture.join();
        Optional<String> video = videoFuture.join();
        String certification = certificationFuture.join();

        return AdminMovieDetailResponse.builder()
                .movieId(movieId)
                .title(detail.getTitle())
                .director(director.orElseGet(() -> TMDBCasts.builder().name("N/A").profilePath("N/A").build()))
                .runtime(detail.getRuntime())
                .posterPath(imageUrl + detail.getPosterPath())
                .releaseDate(detail.getReleaseDate())
                .certification(certification)
                .country(detail.getOriginCountry().get(0))
                .plot(detail.getOverview())
                .youtube(video.orElse("N/A"))
                .stillCuts(images)
                .actors(actors)
                .categories(detail.getGenres().stream().map(TMDBGenre::getName).collect(Collectors.toList()))
                .build();
    }

    private TMDBMovieDetailResponse getMovieDetail(String url) {
        return webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBMovieDetailResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();
    }

    private List<TMDBCasts> getMovieActors(String url) {
        TMDBMovieCredits dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBMovieCredits.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null || dates.getCast().isEmpty()) {
            return Collections.emptyList();
        }

        return dates.getCast().stream()
                .filter(data -> data.getKnownForDepartment().equals("Acting"))
                .sorted(Comparator.comparing(TMDBMovieCreditsResponse::getPopularity).reversed())
                .limit(10)
                .map(data -> TMDBCasts.builder()
                        .tmdbCasterId(data.getTmdbCasterId())
                        .name(data.getName())
                        .profilePath(imageUrl + data.getProfilePath())
                        .build())
                .collect(Collectors.toList());
    }

    private Optional<TMDBCasts> getMovieDirector(String url) {
        TMDBMovieCredits dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBMovieCredits.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null || dates.getCrew().isEmpty()) {
            return Optional.empty();
        }

        return dates.getCrew().stream()
                .filter(data -> data.getJob().equals("Director"))
                .findFirst()
                .map(data -> TMDBCasts.builder()
                        .tmdbCasterId(data.getTmdbCasterId())
                        .name(data.getName())
                        .profilePath(imageUrl + data.getProfilePath())
                        .build());
    }

    private List<String> getMovieImages(String url) {
        TMDBMovieImagesResponse dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBMovieImagesResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null || dates.getBackdrops().isEmpty()) {
            return Collections.emptyList();
        }

        return dates.getBackdrops().stream()
                .map(data-> imageUrl + data.getFilePath())
                .limit(5)
                .collect(Collectors.toList());
    }

    private Optional<String> getMovieVideos(String url) {
        TMDBMovieVideo dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBMovieVideo.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null || dates.getResults().isEmpty()) {
            return Optional.empty();
        }

        String youtube = "https://www.youtube.com/watch?v=";

        return dates.getResults().stream()
                .filter(data -> data.getType().equals("Trailer"))
                .findFirst()
                .map(data -> youtube + data.getKey());
    }

    private String getCertification(String url) {
        TMDBCertificationResponse dates = webClient.get()
                .uri(url)
                .header("accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("잘못된 TMDB API 요청입니다. 요청 URL 또는 매개변수를 확인하세요."))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TMDB 요청 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                )
                .bodyToMono(TMDBCertificationResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .block();

        if (dates == null || dates.getResults().isEmpty()) {
            return "N/A";
        }

        return dates.getResults().stream()
                .filter(data-> data.getIso31661().equals("KR"))
                .flatMap(data-> data.getReleaseDates().stream())
                .findFirst()
                .map(TMDBCertification::getCertification)
                .orElse("N/A");
    }

    // url
    private String createSearchUrl(String movieName) {
        String baseUrl = "https://api.themoviedb.org/3/search/movie?api_key=";
        String encodedMovieName = URLEncoder.encode(movieName, StandardCharsets.UTF_8);
        String query = "&query=" + encodedMovieName + "&language=ko-KR";
        return baseUrl + apiKey + query;
    }

    private String createMovieDetailUrl(Long movieId) {
        String baseUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=";
        String query = "&language=ko-KR";
        return baseUrl + apiKey + query;
    }

    private String createMovieAboutUrl(Long movieId, String word) {
        return  "https://api.themoviedb.org/3/movie/" + movieId + "/" + word + "?api_key=" + apiKey + "&language=ko-KR";
    }

    private String createMovieImagesUrl(Long movieId) {
        return "https://api.themoviedb.org/3/movie/" + movieId + "/images?api_key=" + apiKey;
    }

    private String createMovieCertificationUrl(Long movieId) {
        String baseUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/release_dates?api_key=";
        return baseUrl + apiKey;
    }
}
