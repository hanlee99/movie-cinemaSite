package com.example.demo.service;

import com.example.demo.dto.movie.kmdb.KmdbMovieDto;
import com.example.demo.dto.movie.kmdb.KmdbPersonDto;
import com.example.demo.entity.MovieEntity;
import com.example.demo.repository.MoviePersonRepository;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//엔티티 저장 테스트
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MovieSyncServiceTest {
    @Autowired
    private MovieSyncService movieSyncService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @BeforeEach
    void setUp() {
        moviePersonRepository.deleteAll();
        personRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void 중복된_영화는_저장_안됨() {
        // given
        KmdbMovieDto dto1 = KmdbMovieDto.builder()
                .docId("F63242")
                .title("극장판 귀멸의 칼날 v1")
                .genre("액션")
                .build();

        movieSyncService.saveSingleMovie(dto1);

        // when - 같은 docId로 다시 저장 시도
        KmdbMovieDto dto2 = KmdbMovieDto.builder()
                .docId("F63242")
                .title("극장판 귀멸의 칼날 v2")
                .genre("드라마")
                .build();

        movieSyncService.saveSingleMovie(dto2);

        // then
        assertThat(movieRepository.count()).isEqualTo(1);
        MovieEntity saved = movieRepository.findAll().get(0);
        assertThat(saved.getTitle()).isEqualTo("극장판 귀멸의 칼날 v1");
        assertThat(saved.getGenre()).isEqualTo("액션");
    }

    @Test
    void 여러_영화_저장시_중복_필터링() {
        // given
        List<KmdbMovieDto> dtos = List.of(
                KmdbMovieDto.builder()
                        .docId("F001")
                        .title("영화1")
                        .build(),
                KmdbMovieDto.builder()
                        .docId("F002")
                        .title("영화2")
                        .build(),
                KmdbMovieDto.builder()
                        .docId("F001")  // 중복
                        .title("영화1 다른버전")
                        .build()
        );

        // when
        movieSyncService.saveMovies(dtos);

        // then - 2개만 저장
        assertThat(movieRepository.count()).isEqualTo(2);
    }

    @Test
    void 인물_정보도_함께_저장() {
        // given
        KmdbPersonDto director = KmdbPersonDto.builder()
                .personId("P001")
                .name("감독1")
                .nameEn("Director1")
                .roleGroup("감독")
                .build();

        KmdbPersonDto actor = KmdbPersonDto.builder()
                .personId("P002")
                .name("배우1")
                .roleGroup("출연")
                .roleName("주인공")
                .build();

        KmdbMovieDto dto = KmdbMovieDto.builder()
                .docId("F001")
                .title("테스트 영화")
                .staffs(List.of(director, actor))
                .build();

        // when
        movieSyncService.saveSingleMovie(dto);

        // then
        assertThat(movieRepository.count()).isEqualTo(1);
        assertThat(personRepository.count()).isEqualTo(2);
        assertThat(moviePersonRepository.count()).isEqualTo(2);
    }

    @Test
    void 같은_인물은_재사용() {
        // given - 첫 번째 영화
        KmdbPersonDto director1 = KmdbPersonDto.builder()
                .personId("P001")
                .name("감독1")
                .roleGroup("감독")
                .build();

        KmdbMovieDto movie1 = KmdbMovieDto.builder()
                .docId("F001")
                .title("영화1")
                .staffs(List.of(director1))
                .build();

        movieSyncService.saveSingleMovie(movie1);

        // when - 두 번째 영화 (같은 감독)
        KmdbPersonDto director2 = KmdbPersonDto.builder()
                .personId("P001")  // 같은 personId
                .name("감독1")
                .roleGroup("감독")
                .build();

        KmdbMovieDto movie2 = KmdbMovieDto.builder()
                .docId("F002")
                .title("영화2")
                .staffs(List.of(director2))
                .build();

        movieSyncService.saveSingleMovie(movie2);

        // then - 인물은 1명, 관계는 2개
        assertThat(movieRepository.count()).isEqualTo(2);
        assertThat(personRepository.count()).isEqualTo(1);  // 재사용
        assertThat(moviePersonRepository.count()).isEqualTo(2);
    }

    @Test
    void personId_없으면_이름으로_중복_체크() {
        // given
        KmdbPersonDto person1 = KmdbPersonDto.builder()
                .personId("")  // personId 없음
                .name("무명배우")
                .roleGroup("출연")
                .build();

        KmdbMovieDto movie1 = KmdbMovieDto.builder()
                .docId("F001")
                .title("영화1")
                .staffs(List.of(person1))
                .build();

        movieSyncService.saveSingleMovie(movie1);

        // when - 같은 이름
        KmdbPersonDto person2 = KmdbPersonDto.builder()
                .personId("")
                .name("무명배우")  // 같은 이름
                .roleGroup("출연")
                .build();

        KmdbMovieDto movie2 = KmdbMovieDto.builder()
                .docId("F002")
                .title("영화2")
                .staffs(List.of(person2))
                .build();

        movieSyncService.saveSingleMovie(movie2);

        // then - 이름으로 재사용
        assertThat(personRepository.count()).isEqualTo(1);
    }
}
