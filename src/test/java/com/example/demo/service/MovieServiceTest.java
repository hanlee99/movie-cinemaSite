package com.example.demo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MovieServiceTest {
    @Autowired
    private MovieService movieService;

    @Test
    @DisplayName("일간 박스오피스 조회 성공")
    void getDailyBoxOffice_Success() {
        // when
        var result = movieService.getDailyBoxOffice();

        // then
        assertNotNull(result);
        assertNotNull(result.getDailyBoxOfficeList());
    }
}
