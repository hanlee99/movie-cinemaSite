package com.movierang.external.adapter;

import com.movierang.dto.movie.kmdb.KmdbMovieDto;
import com.movierang.external.kmdb.KmdbApiClient;
import com.movierang.external.kmdb.KmdbRequest;
import com.movierang.external.kmdb.KmdbResponse;
import com.movierang.mapper.KmdbMovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KmdbAdapter {
    private final KmdbApiClient kmdbApiClient;
    private final KmdbMovieMapper kmdbMovieMapper;

    public List<KmdbMovieDto> fetchMovies(String title) {
        KmdbRequest req = KmdbRequest.builder()
                .title(title)
                .detail("Y")
                .build();

        KmdbResponse response = kmdbApiClient.searchMovies(req);
        if (response == null || response.getData() == null) return List.of();

        return response.getData().stream()
                .flatMap(data -> data.getResult().stream())
                .map(kmdbMovieMapper::toDto)
                .toList();
    }

    public List<KmdbMovieDto> searchMovies(String releaseDts, String releaseDte, int listCount, int startCount) {
        KmdbRequest req = KmdbRequest.builder()
                .releaseDts(releaseDts)
                .releaseDte(releaseDte)
                .listCount(listCount)
                .startCount(startCount)
                .detail("Y")
                .build();

        KmdbResponse response = kmdbApiClient.searchMovies(req);
        if (response == null || response.getData() == null) return List.of();

        return response.getData().stream()
                .flatMap(data -> data.getResult().stream())
                .map(kmdbMovieMapper::toDto)
                .toList();
    }
}
