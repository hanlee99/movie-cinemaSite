package com.movierang.unit.dto;

import com.movierang.dto.movie.boxoffice.BoxOfficeItemDto;
import com.movierang.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.movierang.external.kobis.KobisDailyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DailyBoxOfficeResultDto 단위 테스트
 * - KOBIS 외부 DTO를 내부 DTO로 변환하는 로직 테스트
 */
@DisplayName("DailyBoxOfficeResultDto 단위 테스트")
class DailyBoxOfficeResultDtoTest {

    @Test
    @DisplayName("KOBIS 응답을 내부 DTO로 변환")
    void KOBIS_응답을_내부_DTO로_변환() {
        // given
        KobisDailyResponse.DailyBoxOfficeItem item1 = new KobisDailyResponse.DailyBoxOfficeItem();
        item1.setRank("1");
        item1.setMovieCd("20254375");
        item1.setMovieNm("프레데터: 죽음의 땅");
        item1.setOpenDt("2025-11-05");
        item1.setSalesAmt("180241700");
        item1.setSalesAcc("2489952490");
        item1.setAudiCnt("17628");
        item1.setAudiAcc("238748");

        KobisDailyResponse.DailyBoxOfficeItem item2 = new KobisDailyResponse.DailyBoxOfficeItem();
        item2.setRank("2");
        item2.setMovieCd("20253852");
        item2.setMovieNm("퍼스트 라이드");
        item2.setOpenDt("20251029");  // yyyyMMdd 형식도 테스트
        item2.setSalesAmt("110506000");
        item2.setSalesAcc("5467692830");
        item2.setAudiCnt("12074");
        item2.setAudiAcc("590291");

        KobisDailyResponse.BoxOfficeResult boxOfficeResult = new KobisDailyResponse.BoxOfficeResult();
        boxOfficeResult.setBoxofficeType("일별 박스오피스");
        boxOfficeResult.setShowRange("20251110~20251110");
        boxOfficeResult.setDailyBoxOfficeList(java.util.List.of(item1, item2));

        KobisDailyResponse response = new KobisDailyResponse();
        response.setBoxOfficeResult(boxOfficeResult);

        // when
        DailyBoxOfficeResultDto result = DailyBoxOfficeResultDto.from(response);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBoxOfficeType()).isEqualTo("일별 박스오피스");
        assertThat(result.getShowRange()).isEqualTo("2025-11-10");  // 포맷팅 확인
        assertThat(result.getDailyBoxOfficeList()).hasSize(2);

        // 첫 번째 영화 검증
        BoxOfficeItemDto first = result.getDailyBoxOfficeList().get(0);
        assertThat(first.getRank()).isEqualTo(1);  // String → int 변환
        assertThat(first.getMovieCd()).isEqualTo("20254375");
        assertThat(first.getTitle()).isEqualTo("프레데터: 죽음의 땅");
        assertThat(first.getOpenDt()).isEqualTo(LocalDate.of(2025, 11, 5));
        assertThat(first.getSalesAmt()).isEqualTo(180241700L);  // String → Long 변환
        assertThat(first.getSalesAcc()).isEqualTo(2489952490L);
        assertThat(first.getAudiCnt()).isEqualTo(17628L);
        assertThat(first.getAudiAcc()).isEqualTo(238748L);

        // 두 번째 영화 검증 (yyyyMMdd 날짜 파싱 확인)
        BoxOfficeItemDto second = result.getDailyBoxOfficeList().get(1);
        assertThat(second.getRank()).isEqualTo(2);
        assertThat(second.getTitle()).isEqualTo("퍼스트 라이드");
        assertThat(second.getOpenDt()).isEqualTo(LocalDate.of(2025, 10, 29));
    }

    @Test
    @DisplayName("showRange 날짜 포맷팅")
    void showRange_날짜_포맷팅_테스트() {
        // given
        KobisDailyResponse.BoxOfficeResult boxOfficeResult = new KobisDailyResponse.BoxOfficeResult();
        boxOfficeResult.setBoxofficeType("일별 박스오피스");
        boxOfficeResult.setShowRange("20241231~20241231");
        boxOfficeResult.setDailyBoxOfficeList(java.util.List.of());

        KobisDailyResponse response = new KobisDailyResponse();
        response.setBoxOfficeResult(boxOfficeResult);

        // when
        DailyBoxOfficeResultDto result = DailyBoxOfficeResultDto.from(response);

        // then
        assertThat(result.getShowRange()).isEqualTo("2024-12-31");
    }

    @Test
    @DisplayName("BoxOfficeItemDto 날짜 파싱 - yyyy-MM-dd 형식")
    void BoxOfficeItemDto_날짜_파싱_yyyy_MM_dd() {
        // given
        KobisDailyResponse.DailyBoxOfficeItem item = new KobisDailyResponse.DailyBoxOfficeItem();
        item.setRank("1");
        item.setMovieCd("123");
        item.setMovieNm("테스트");
        item.setOpenDt("2025-01-15");
        item.setSalesAmt("1000");
        item.setSalesAcc("10000");
        item.setAudiCnt("100");
        item.setAudiAcc("1000");

        // when
        BoxOfficeItemDto result = BoxOfficeItemDto.from(item);

        // then
        assertThat(result.getOpenDt()).isEqualTo(LocalDate.of(2025, 1, 15));
    }

    @Test
    @DisplayName("BoxOfficeItemDto 날짜 파싱 - yyyyMMdd 형식")
    void BoxOfficeItemDto_날짜_파싱_yyyyMMdd() {
        // given
        KobisDailyResponse.DailyBoxOfficeItem item = new KobisDailyResponse.DailyBoxOfficeItem();
        item.setRank("1");
        item.setMovieCd("123");
        item.setMovieNm("테스트");
        item.setOpenDt("20250115");
        item.setSalesAmt("1000");
        item.setSalesAcc("10000");
        item.setAudiCnt("100");
        item.setAudiAcc("1000");

        // when
        BoxOfficeItemDto result = BoxOfficeItemDto.from(item);

        // then
        assertThat(result.getOpenDt()).isEqualTo(LocalDate.of(2025, 1, 15));
    }
}
