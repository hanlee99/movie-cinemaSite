package com.movierang.service;

import com.movierang.dto.movie.kmdb.KmdbMovieDto;
import com.movierang.dto.movie.kmdb.KmdbPersonDto;
import com.movierang.entity.MovieEntity;
import com.movierang.entity.MoviePersonEntity;
import com.movierang.entity.PersonEntity;
import com.movierang.external.adapter.KmdbAdapter;
import com.movierang.repository.MoviePersonRepository;
import com.movierang.repository.MovieRepository;
import com.movierang.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieSyncService {
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final MoviePersonRepository moviePersonRepository;
    private final KmdbAdapter kmdbAdapter;

    public void saveMovies(List<KmdbMovieDto> dtos) {
        log.info("Starting to save movies. count={}", dtos.size());
        int success = 0, fail = 0;

        for (KmdbMovieDto dto : dtos) {
            try {
                saveSingleMovie(dto);
                success++;
            } catch (Exception e) {
                fail++;
                log.error("Failed to save movie. title={}", dto.getTitle(), e);
            }
        }

        log.info("Movie save completed. success={}, failed={}", success, fail);
    }

    public void saveSingleMovie(KmdbMovieDto dto) {
        if (movieRepository.existsByDocId(dto.getDocId())) {
            log.debug("Movie already exists. docId={}", dto.getDocId());
            return;
        }

        MovieEntity movie = MovieEntity.from(dto);
        movieRepository.save(movie);
        log.debug("Movie saved. title={}, docId={}", movie.getTitle(), movie.getDocId());

        if (dto.getStaffs() != null) {
            savePeople(movie, dto.getStaffs());
        }
    }

    private void savePeople(MovieEntity movie, List<KmdbPersonDto> staffs) {
        for (KmdbPersonDto staff : staffs) {
            String personKey = (staff.getPersonId() == null || staff.getPersonId().isBlank())
                    ? staff.getName()
                    : staff.getPersonId();

            PersonEntity person = personRepository.findByKmdbPersonId(personKey)
                    .orElseGet(() -> personRepository.save(
                            PersonEntity.builder()
                                    .kmdbPersonId(personKey)
                                    .name(staff.getName())
                                    .nameEn(staff.getNameEn())
                                    .build()
                    ));

            moviePersonRepository.save(
                    MoviePersonEntity.builder()
                            .movie(movie)
                            .person(person)
                            .roleGroup(staff.getRoleGroup())
                            .roleName(staff.getRoleName())
                            .build()
            );
        }
    }

    @Transactional
    public void syncMovieByTitle(String title) {
        log.info("Start sync by title: {}", title);

        // 1) KMDB에서 영화 검색
        List<KmdbMovieDto> movies = kmdbAdapter.fetchMovies(title);
        if (movies.isEmpty()) {
            log.warn("No movies found for title: {}", title);
            return;
        }

        log.info("Found {} movies for keyword '{}'", movies.size(), title);

        // 2) DB 저장 (중복 체크 포함)
        saveMovies(movies);

        log.info("Finished syncing by title: {}", title);
    }

    public void syncMoviesByYear(int year) {
        String releaseDts = year + "0101";
        String releaseDte = year + "1231";
        int listCount = 500;
        int startCount = 0;
        int total = 0;

        log.info("Starting movie sync. year={}", year);

        while (true) {
            List<KmdbMovieDto> movies = kmdbAdapter.searchMovies(releaseDts, releaseDte, listCount, startCount);
            if (movies.isEmpty()) break;

            saveMovies(movies);
            total += movies.size();
            startCount += listCount;

            log.info("Movie batch saved. batchSize={}, totalSaved={}", movies.size(), total);
        }

        log.info("Movie sync completed. year={}, totalCount={}", year, total);
    }
    public void syncMoviesByDay(String releaseDts, String releaseDte) {
        int listCount = 500;
        int startCount = 0;
        int total = 0;

        log.info("Starting movie sync. releaseDts={}, releaseDte={}", releaseDts, releaseDte);

        while (true) {
            List<KmdbMovieDto> movies = kmdbAdapter.searchMovies(releaseDts, releaseDte, listCount, startCount);
            if (movies.isEmpty()) break;

            saveMovies(movies);
            total += movies.size();
            startCount += listCount;

            log.info("Movie batch saved. batchSize={}, totalSaved={}", movies.size(), total);
        }

        log.info("Movie sync completed. releaseDts={}, releaseDte={}, totalCount={}", releaseDts, releaseDte, total);
    }
}
