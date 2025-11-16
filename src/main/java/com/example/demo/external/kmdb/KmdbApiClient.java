package com.example.demo.external.kmdb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KmdbApiClient {
    private final WebClient kmdbWebClient;

    @Value("${api.kmdb.key}")
    private String KMDB_API_KEY;

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public KmdbResponse searchMovies(KmdbRequest req) {
        String json = kmdbWebClient.get()
                .uri(uri -> uri
                        .path("search_json2.jsp")
                        .queryParam("ServiceKey", KMDB_API_KEY)
                        .queryParam("collection", "kmdb_new2")
                        .queryParam("detail", req.getDetail())
                        .queryParamIfPresent("query", Optional.ofNullable(req.getQuery()))
                        .queryParamIfPresent("title", Optional.ofNullable(req.getTitle()))
                        .queryParamIfPresent("director", Optional.ofNullable(req.getDirector()))
                        .queryParamIfPresent("actor", Optional.ofNullable(req.getActor()))
                        .queryParamIfPresent("genre", Optional.ofNullable(req.getGenre()))
                        .queryParamIfPresent("nation", Optional.ofNullable(req.getNation()))
                        .queryParamIfPresent("releaseDts", Optional.ofNullable(req.getReleaseDts()))
                        .queryParamIfPresent("releaseDte", Optional.ofNullable(req.getReleaseDte()))
                        .queryParamIfPresent("movieId", Optional.ofNullable(req.getMovieId()))
                        .queryParamIfPresent("movieSeq", Optional.ofNullable(req.getMovieSeq()))
                        .queryParamIfPresent("listCount", Optional.ofNullable(req.getListCount()))
                        .queryParamIfPresent("startCount", Optional.ofNullable(req.getStartCount()))
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null || !json.trim().startsWith("{")) {
            throw new RuntimeException("KMDB 응답이 비정상입니다.");
        }

        try {
            return mapper.readValue(json, KmdbResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("KMDB JSON 파싱 실패", e);
        }
    }
}
