package com.movierang.unit.dto;

import com.movierang.dto.movie.DailyBoxOfficeResponse;
import com.movierang.dto.movie.boxoffice.BoxOfficeItemDto;
import com.movierang.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.movierang.entity.MovieEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DailyBoxOfficeResponse 단위 테스트
 * - 박스오피스 내부 DTO와 DB 영화 정보 병합 테스트
 */
@DisplayName("DailyBoxOfficeResponse 단위 테스트")
class DailyBoxOfficeResponseTest {

    @Test
    @DisplayName("박스오피스와 DB 영화정보 매핑 성공")
    void 박스오피스와_DB_영화정보_매핑_테스트() {
        // given - 박스오피스 정보
        BoxOfficeItemDto item1 = BoxOfficeItemDto.builder()
                .rank(1)
                .movieCd("20254375")
                .title("프레데터: 죽음의 땅")
                .openDt(LocalDate.of(2025, 11, 5))
                .salesAmt(180241700L)
                .salesAcc(2489952490L)
                .audiCnt(17628L)
                .audiAcc(238748L)
                .build();

        BoxOfficeItemDto item2 = BoxOfficeItemDto.builder()
                .rank(2)
                .movieCd("20253852")
                .title("퍼스트 라이드")
                .openDt(LocalDate.of(2025, 10, 29))
                .salesAmt(110506000L)
                .salesAcc(5467692830L)
                .audiCnt(12074L)
                .audiAcc(590291L)
                .build();

        DailyBoxOfficeResultDto boxOffice = DailyBoxOfficeResultDto.builder()
                .boxOfficeType("일별 박스오피스")
                .showRange("2025-11-10")
                .dailyBoxOfficeList(List.of(item1, item2))
                .build();

        // given - DB에서 가져온 영화 정보
        MovieEntity movie1 = new MovieEntity();
        movie1.setDocId("KMDB001");
        movie1.setTitle("프레데터: 죽음의 땅");
        movie1.setPoster("http://poster1.jpg");
        movie1.setGenre("액션");

        MovieEntity movie2 = new MovieEntity();
        movie2.setDocId("KMDB002");
        movie2.setTitle("퍼스트 라이드");
        movie2.setPoster("http://poster2.jpg");
        movie2.setGenre("드라마");

        Map<String, MovieEntity> movieMap = new HashMap<>();
        movieMap.put("프레데터: 죽음의 땅", movie1);
        movieMap.put("퍼스트 라이드", movie2);

        // when
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, movieMap);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getBoxOfficeType()).isEqualTo("일별 박스오피스");
        assertThat(response.getShowRange()).isEqualTo("2025-11-10");
        assertThat(response.getMovies()).hasSize(2);

        // 1위 영화 검증 (박스오피스 + DB 정보)
        DailyBoxOfficeResponse.Movie first = response.getMovies().get(0);
        assertThat(first.getRank()).isEqualTo(1);
        assertThat(first.getMovieCd()).isEqualTo("20254375");
        assertThat(first.getDocId()).isEqualTo("KMDB001");  // DB에서 가져온 정보
        assertThat(first.getTitle()).isEqualTo("프레데터: 죽음의 땅");
        assertThat(first.getPoster()).isEqualTo("http://poster1.jpg");  // DB
        assertThat(first.getGenre()).isEqualTo("액션");  // DB
        assertThat(first.getSalesAmt()).isEqualTo(180241700L);  // 박스오피스
        assertThat(first.getAudiCnt()).isEqualTo(17628L);  // 박스오피스

        // 2위 영화 검증
        DailyBoxOfficeResponse.Movie second = response.getMovies().get(1);
        assertThat(second.getRank()).isEqualTo(2);
        assertThat(second.getTitle()).isEqualTo("퍼스트 라이드");
        assertThat(second.getPoster()).isEqualTo("http://poster2.jpg");
        assertThat(second.getGenre()).isEqualTo("드라마");
    }

    @Test
    @DisplayName("DB에 영화정보 없을때 null 처리")
    void DB에_영화정보_없을때_null_처리() {
        // given - 박스오피스 정보
        BoxOfficeItemDto item = BoxOfficeItemDto.builder()
                .rank(1)
                .movieCd("20254375")
                .title("DB에 없는 영화")
                .openDt(LocalDate.of(2025, 11, 5))
                .salesAmt(100000L)
                .salesAcc(1000000L)
                .audiCnt(1000L)
                .audiAcc(10000L)
                .build();

        DailyBoxOfficeResultDto boxOffice = DailyBoxOfficeResultDto.builder()
                .boxOfficeType("일별 박스오피스")
                .showRange("2025-11-10")
                .dailyBoxOfficeList(List.of(item))
                .build();

        // given - 빈 영화 맵 (DB에 해당 영화 없음)
        Map<String, MovieEntity> emptyMovieMap = new HashMap<>();

        // when
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, emptyMovieMap);

        // then
        assertThat(response.getMovies()).hasSize(1);

        DailyBoxOfficeResponse.Movie movie = response.getMovies().get(0);
        assertThat(movie.getTitle()).isEqualTo("DB에 없는 영화");
        assertThat(movie.getDocId()).isNull();  // DB 정보 없음
        assertThat(movie.getPoster()).isNull();  // DB 정보 없음
        assertThat(movie.getGenre()).isNull();  // DB 정보 없음
        assertThat(movie.getSalesAmt()).isEqualTo(100000L);  // 박스오피스 정보는 있음
        assertThat(movie.getAudiCnt()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("일부 영화만 DB에 있을때")
    void 일부_영화만_DB에_있을때() {
        // given
        BoxOfficeItemDto item1 = BoxOfficeItemDto.builder()
                .rank(1)
                .movieCd("123")
                .title("DB에 있는 영화")
                .openDt(LocalDate.of(2025, 11, 5))
                .salesAmt(100000L)
                .salesAcc(1000000L)
                .audiCnt(1000L)
                .audiAcc(10000L)
                .build();

        BoxOfficeItemDto item2 = BoxOfficeItemDto.builder()
                .rank(2)
                .movieCd("456")
                .title("DB에 없는 영화")
                .openDt(LocalDate.of(2025, 11, 5))
                .salesAmt(50000L)
                .salesAcc(500000L)
                .audiCnt(500L)
                .audiAcc(5000L)
                .build();

        DailyBoxOfficeResultDto boxOffice = DailyBoxOfficeResultDto.builder()
                .boxOfficeType("일별 박스오피스")
                .showRange("2025-11-10")
                .dailyBoxOfficeList(List.of(item1, item2))
                .build();

        // DB에 1개 영화만 존재
        MovieEntity movie1 = new MovieEntity();
        movie1.setDocId("KMDB001");
        movie1.setTitle("DB에 있는 영화");
        movie1.setPoster("http://poster.jpg");
        movie1.setGenre("액션");

        Map<String, MovieEntity> movieMap = new HashMap<>();
        movieMap.put("DB에 있는 영화", movie1);

        // when
        DailyBoxOfficeResponse response = DailyBoxOfficeResponse.from(boxOffice, movieMap);

        // then
        assertThat(response.getMovies()).hasSize(2);

        // 첫 번째: DB 정보 있음
        assertThat(response.getMovies().get(0).getPoster()).isNotNull();

        // 두 번째: DB 정보 없음
        assertThat(response.getMovies().get(1).getPoster()).isNull();
    }
}
