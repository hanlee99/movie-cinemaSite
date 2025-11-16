package com.example.demo.movie.boxoffice;

import com.example.demo.external.kobis.KobisDailyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

//코비스 외부 json 매핑 테스트
public class KobisDailyResponseTest { //코비스 api json을 외부 dto로 변환 테스트
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void KOBIS_API_응답_파싱_테스트() throws Exception {
        // given - 실제 KOBIS API 응답 (2025-11-10 기준)
        String json = """
            {
                "boxOfficeResult": {
                    "boxofficeType": "일별 박스오피스",
                    "showRange": "20251110~20251110",
                    "dailyBoxOfficeList": [
                        {
                            "rnum": "1",
                            "rank": "1",
                            "rankInten": "0",
                            "rankOldAndNew": "OLD",
                            "movieCd": "20254375",
                            "movieNm": "프레데터: 죽음의 땅",
                            "openDt": "2025-11-05",
                            "salesAmt": "180241700",
                            "salesShare": "25.1",
                            "salesInten": "-401404890",
                            "salesChange": "-69",
                            "salesAcc": "2489952490",
                            "audiCnt": "17628",
                            "audiInten": "-37368",
                            "audiChange": "-67.9",
                            "audiAcc": "238748",
                            "scrnCnt": "846",
                            "showCnt": "2930"
                        },
                        {
                            "rnum": "2",
                            "rank": "2",
                            "rankInten": "0",
                            "rankOldAndNew": "OLD",
                            "movieCd": "20253852",
                            "movieNm": "퍼스트 라이드",
                            "openDt": "2025-10-29",
                            "salesAmt": "110506000",
                            "salesShare": "15.4",
                            "salesInten": "-304072670",
                            "salesChange": "-73.3",
                            "salesAcc": "5467692830",
                            "audiCnt": "12074",
                            "audiInten": "-30240",
                            "audiChange": "-71.5",
                            "audiAcc": "590291",
                            "scrnCnt": "698",
                            "showCnt": "1922"
                        }
                    ]
                }
            }
            """;

        // when
        KobisDailyResponse result = objectMapper.readValue(json, KobisDailyResponse.class);

        // then - 전체 구조 검증
        assertThat(result).isNotNull();
        assertThat(result.getBoxOfficeResult()).isNotNull();

        // BoxOfficeResult 검증
        KobisDailyResponse.BoxOfficeResult boxOffice = result.getBoxOfficeResult();
        assertThat(boxOffice.getBoxofficeType()).isEqualTo("일별 박스오피스");
        assertThat(boxOffice.getShowRange()).isEqualTo("20251110~20251110");
        assertThat(boxOffice.getDailyBoxOfficeList()).hasSize(2);

        // 1위 영화 검증
        KobisDailyResponse.DailyBoxOfficeItem first = boxOffice.getDailyBoxOfficeList().get(0);
        assertThat(first.getRank()).isEqualTo("1");
        assertThat(first.getMovieCd()).isEqualTo("20254375");
        assertThat(first.getMovieNm()).isEqualTo("프레데터: 죽음의 땅");
        assertThat(first.getOpenDt()).isEqualTo("2025-11-05");
        assertThat(first.getSalesAmt()).isEqualTo("180241700");
        assertThat(first.getSalesAcc()).isEqualTo("2489952490");
        assertThat(first.getAudiCnt()).isEqualTo("17628");
        assertThat(first.getAudiAcc()).isEqualTo("238748");

        // 2위 영화 검증
        KobisDailyResponse.DailyBoxOfficeItem second = boxOffice.getDailyBoxOfficeList().get(1);
        assertThat(second.getRank()).isEqualTo("2");
        assertThat(second.getMovieNm()).isEqualTo("퍼스트 라이드");
        assertThat(second.getAudiCnt()).isEqualTo("12074");
    }

    @Test
    void 필수_필드만_있어도_파싱_성공() throws Exception {
        // given - 최소 필드만 포함
        String json = """
            {
                "boxOfficeResult": {
                    "boxofficeType": "일별 박스오피스",
                    "showRange": "20251110~20251110",
                    "dailyBoxOfficeList": [
                        {
                            "rank": "1",
                            "movieCd": "20254375",
                            "movieNm": "테스트 영화",
                            "openDt": "2025-11-05",
                            "salesAmt": "100000",
                            "salesAcc": "1000000",
                            "audiCnt": "100",
                            "audiAcc": "1000"
                        }
                    ]
                }
            }
            """;

        // when
        KobisDailyResponse result = objectMapper.readValue(json, KobisDailyResponse.class);

        // then
        assertThat(result.getBoxOfficeResult().getDailyBoxOfficeList()).hasSize(1);
        assertThat(result.getBoxOfficeResult().getDailyBoxOfficeList().get(0).getMovieNm())
                .isEqualTo("테스트 영화");
    }

    @Test
    void 빈_박스오피스_리스트_파싱() throws Exception {
        // given
        String json = """
            {
                "boxOfficeResult": {
                    "boxofficeType": "일별 박스오피스",
                    "showRange": "20251110~20251110",
                    "dailyBoxOfficeList": []
                }
            }
            """;

        // when
        KobisDailyResponse result = objectMapper.readValue(json, KobisDailyResponse.class);

        // then
        assertThat(result.getBoxOfficeResult().getDailyBoxOfficeList()).isEmpty();
    }
}
