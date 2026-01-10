package com.movierang.unit.external;

import com.movierang.external.kmdb.KmdbResponse;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KmdbResponse 단위 테스트
 * - KMDB 외부 API JSON 파싱 테스트
 */
@DisplayName("KmdbResponse 단위 테스트")
class KmdbResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }

    @Test
    @DisplayName("KMDB API 응답 파싱 성공")
    void KMDB_API_응답_파싱_테스트() throws Exception {
        // given
        String json = """
            {
                "TotalCount": 403,
                "Data": [
                    {
                        "CollName": "kmdb_new2",
                        "TotalCount": 403,
                        "Count": 403,
                        "Result": [
                            {
                                "DOCID": "F63242",
                                "movieId": "F",
                                "movieSeq": "63242",
                                "title": " 극장판 귀멸의 칼날: 무한성편",
                                "titleEng": "Demon Slayer: Kimetsu No Yaiba Infinity Castle Arc Part 1",
                                "prodYear": "2025",
                                "nation": "일본",
                                "genre": "액션",
                                "runtime": "155",
                                "rating": "15세이상관람가",
                                "repRlsDate": "20250822",
                                "posters": "http://file.koreafilm.or.kr/thm/02/99/18/82/tn_DPF030855.jpg",
                                "plots": {
                                    "plot": [
                                        {
                                            "plotLang": "한국어",
                                            "plotText": "다가오는 혈귀와의 결전"
                                        }
                                    ]
                                },
                                "directors": {
                                    "director": [
                                        {
                                            "directorNm": "소토자키 하루오",
                                            "directorEnNm": "Sotozaki Haruo",
                                            "directorId": "00232191"
                                        }
                                    ]
                                },
                                "actors": {
                                    "actor": [
                                        {
                                            "actorNm": "하나에 나츠키",
                                            "actorEnNm": "",
                                            "actorId": ""
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
            """;

        // when
        KmdbResponse result = objectMapper.readValue(json, KmdbResponse.class);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(403);
        assertThat(result.getData()).hasSize(1);

        KmdbResponse.Data data = result.getData().get(0);
        assertThat(data.getCollName()).isEqualTo("kmdb_new2");
        assertThat(data.getResult()).hasSize(1);

        KmdbResponse.Result movie = data.getResult().get(0);
        assertThat(movie.getDOCID()).isEqualTo("F63242");
        assertThat(movie.getTitle()).contains("귀멸의 칼날");
        assertThat(movie.getGenre()).isEqualTo("액션");
        assertThat(movie.getRepRlsDate()).isEqualTo("20250822");

        // 줄거리
        assertThat(movie.getPlots().getPlot()).hasSize(1);
        assertThat(movie.getPlots().getPlot().get(0).getPlotText()).contains("혈귀");

        // 감독
        assertThat(movie.getDirectors().getDirector()).hasSize(1);
        assertThat(movie.getDirectors().getDirector().get(0).getDirectorNm()).isEqualTo("소토자키 하루오");

        // 배우
        assertThat(movie.getActors().getActor()).hasSize(1);
        assertThat(movie.getActors().getActor().get(0).getActorNm()).isEqualTo("하나에 나츠키");
    }

    @Test
    @DisplayName("빈 결과 파싱 성공")
    void 빈_결과_파싱() throws Exception {
        String json = """
            {
                "TotalCount": 0,
                "Data": []
            }
            """;

        KmdbResponse result = objectMapper.readValue(json, KmdbResponse.class);

        assertThat(result.getTotalCount()).isEqualTo(0);
        assertThat(result.getData()).isEmpty();
    }
}
