package com.example.demo.movie.boxoffice;

import com.example.demo.dto.movie.DailyBoxOfficeResponse;
import com.example.demo.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


//박스오피스 통합 테스트
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BoxOfficeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void 박스오피스_API_호출_성공() throws Exception {
        // given
        DailyBoxOfficeResponse mockResponse = createMockResponse();
        when(movieService.getDailyBoxOfficeWithMovieInfo())
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/movie/box-office/daily"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boxOfficeType").value("일별 박스오피스"))
                .andExpect(jsonPath("$.showRange").value("2025-11-10"))
                .andExpect(jsonPath("$.movies").isArray())
                .andExpect(jsonPath("$.movies[0].rank").value(1))
                .andExpect(jsonPath("$.movies[0].title").value("프레데터: 죽음의 땅"))
                .andExpect(jsonPath("$.movies[0].poster").value("http://poster.jpg"))
                .andExpect(jsonPath("$.movies[0].genre").value("액션"))
                .andExpect(jsonPath("$.movies[0].docId").value("KMDB001"));
    }

    private DailyBoxOfficeResponse createMockResponse() {
        DailyBoxOfficeResponse.Movie movie = DailyBoxOfficeResponse.Movie.builder()
                .rank(1)
                .movieCd("123")
                .docId("KMDB001")
                .title("프레데터: 죽음의 땅")
                .poster("http://poster.jpg")
                .genre("액션")
                .openDt(LocalDate.now())
                .salesAmt(1000L)
                .salesAcc(10000L)
                .audiCnt(100L)
                .audiAcc(1000L)
                .build();

        return DailyBoxOfficeResponse.builder()
                .boxOfficeType("일별 박스오피스")
                .showRange("2025-11-10")
                .movies(List.of(movie))
                .build();
    }
}
