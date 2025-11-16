package com.example.demo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {
    @Test
    @DisplayName("MovieNotFoundException 생성 테스트")
    void createMovieNotFoundException() {
        // given
        Long id = -1L;

        // when
        MovieNotFoundException exception = new MovieNotFoundException(id);

        // then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("-1")); // 메시지에 -1이 포함되어 있는지만 확인
    }

    @Test
    @DisplayName("ExternalApiException 생성 테스트")
    void createExternalApiException() {
        // given
        String message = "외부 API 호출 실패";

        // when
        ExternalApiException exception = new ExternalApiException(message);

        // then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}
