package com.example.demo.external.kmdb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

/**
 * HTTP client for interacting with the KMDB (Korean Movie Database) API.
 *
 * <p>KMDB is Korea's comprehensive movie database that provides detailed information about movies,
 * including metadata, cast and crew, posters, plot summaries, ratings, and more. This client
 * handles all communication with the KMDB REST API, including query building, request execution,
 * and response parsing.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Search movies by title, director, actor, genre, nation, or release date</li>
 *   <li>Retrieve detailed movie information by movieId or movieSeq</li>
 *   <li>Support for pagination via listCount and startCount parameters</li>
 *   <li>Automatic JSON deserialization with case-insensitive property matching</li>
 *   <li>Graceful handling of unknown properties in API responses</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 * <p>Required application properties:
 * <pre>
 * api.kmdb.key: Your KMDB API service key (obtain from KMDB API portal)
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Search by title
 * KmdbRequest titleSearch = KmdbRequest.builder()
 *     .title("기생충")
 *     .detail("Y")
 *     .build();
 * KmdbResponse response = kmdbApiClient.searchMovies(titleSearch);
 *
 * // Search by release date range
 * KmdbRequest dateRangeSearch = KmdbRequest.builder()
 *     .releaseDts("20230101")
 *     .releaseDte("20231231")
 *     .listCount(20)
 *     .startCount(0)
 *     .detail("Y")
 *     .build();
 * KmdbResponse movies = kmdbApiClient.searchMovies(dateRangeSearch);
 *
 * // Search by multiple criteria
 * KmdbRequest multiSearch = KmdbRequest.builder()
 *     .director("봉준호")
 *     .genre("드라마")
 *     .nation("한국")
 *     .detail("Y")
 *     .build();
 * KmdbResponse filteredMovies = kmdbApiClient.searchMovies(multiSearch);
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This component is thread-safe. The WebClient is immutable and the ObjectMapper is stateless.
 * Multiple threads can safely invoke {@link #searchMovies(KmdbRequest)} concurrently.
 *
 * <h2>Error Handling</h2>
 * <p>This client performs basic validation of API responses and throws RuntimeException for:
 * <ul>
 *   <li>Invalid JSON responses (responses not starting with '{')</li>
 *   <li>JSON parsing failures</li>
 *   <li>Network errors from WebClient</li>
 * </ul>
 *
 * @see KmdbRequest for available search parameters
 * @see KmdbResponse for response structure
 * @see <a href="https://www.kmdb.or.kr/info/api/apiDetail/6">KMDB API Documentation</a>
 * @author Cinema Site Team
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class KmdbApiClient {

    /**
     * Pre-configured WebClient for making HTTP requests to KMDB API endpoints.
     *
     * <p>The WebClient is injected and should be configured with the KMDB base URL
     * in the application's WebClient configuration bean.
     */
    private final WebClient kmdbWebClient;

    /**
     * KMDB API service key for authentication.
     *
     * <p>This key is required for all API requests and is obtained from the KMDB API portal.
     * The value is injected from the application property {@code api.kmdb.key}.
     *
     * @see <a href="https://www.kmdb.or.kr/info/api/apiServiceInfo">KMDB API Key Registration</a>
     */
    @Value("${api.kmdb.key}")
    private String KMDB_API_KEY;

    /**
     * Jackson ObjectMapper configured for lenient JSON parsing of KMDB API responses.
     *
     * <p>Configuration details:
     * <ul>
     *   <li>{@link MapperFeature#ACCEPT_CASE_INSENSITIVE_PROPERTIES} - Allows mapping JSON
     *       properties to Java fields regardless of case differences (e.g., "MovieId" → "movieId")</li>
     *   <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} disabled - Ignores extra
     *       fields in API responses that don't map to Java fields, making the client resilient
     *       to API changes</li>
     * </ul>
     *
     * <p>This mapper is stateless and thread-safe for concurrent use.
     */
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Searches for movies in the KMDB database based on the provided search criteria.
     *
     * <p>This method constructs and executes an HTTP GET request to the KMDB API's
     * {@code search_json2.jsp} endpoint with the specified search parameters. The request
     * is executed synchronously using {@link WebClient#block()}.
     *
     * <h3>Search Behavior</h3>
     * <ul>
     *   <li>If {@code movieId} or {@code movieSeq} is provided, performs a detail lookup for that specific movie</li>
     *   <li>Otherwise, performs a search using the provided criteria (title, director, actor, etc.)</li>
     *   <li>All parameters are optional and can be combined for more specific searches</li>
     *   <li>Empty/null parameters are automatically excluded from the request via {@code queryParamIfPresent}</li>
     * </ul>
     *
     * <h3>API Endpoint</h3>
     * <pre>
     * GET /search_json2.jsp?ServiceKey={key}&collection=kmdb_new2&detail={Y|N}&...
     * </pre>
     *
     * <h3>Response Validation</h3>
     * <p>The method validates that the response:
     * <ol>
     *   <li>Is not null</li>
     *   <li>Starts with '{' (valid JSON object)</li>
     * </ol>
     *
     * @param req the search request containing search criteria and pagination parameters.
     *            All fields are optional except {@code detail} which defaults to "N".
     *            See {@link KmdbRequest} for available parameters.
     * @return a {@link KmdbResponse} containing the search results with movie data,
     *         metadata, and pagination information
     * @throws RuntimeException if the KMDB API returns an invalid response (non-JSON format)
     * @throws RuntimeException if JSON parsing fails due to malformed response
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException
     *         if the API returns an HTTP error status (4xx or 5xx)
     * @throws org.springframework.web.reactive.function.client.WebClientRequestException
     *         if a network error occurs during the request
     *
     * @see KmdbRequest for detailed parameter descriptions
     * @see KmdbResponse for response structure
     *
     * @example
     * <pre>{@code
     * // Basic title search
     * KmdbRequest request = KmdbRequest.builder()
     *     .title("기생충")
     *     .detail("Y")
     *     .build();
     * KmdbResponse response = searchMovies(request);
     *
     * // Search with pagination
     * KmdbRequest pagedRequest = KmdbRequest.builder()
     *     .query("액션")
     *     .listCount(10)
     *     .startCount(0)
     *     .detail("N")
     *     .build();
     * KmdbResponse firstPage = searchMovies(pagedRequest);
     * }</pre>
     */
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
