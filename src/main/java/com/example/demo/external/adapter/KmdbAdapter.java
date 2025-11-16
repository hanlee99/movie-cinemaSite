package com.example.demo.external.adapter;

import com.example.demo.dto.movie.kmdb.KmdbMovieDto;
import com.example.demo.external.kmdb.KmdbApiClient;
import com.example.demo.external.kmdb.KmdbRequest;
import com.example.demo.external.kmdb.KmdbResponse;
import com.example.demo.mapper.KmdbMovieMapper;
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
                .map(kmdbMovieMapper::toDto)  // ✅ toInternalDto → toDto
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
                .map(kmdbMovieMapper::toDto)  // ✅ toInternalDto → toDto
                .toList();
    }
}
