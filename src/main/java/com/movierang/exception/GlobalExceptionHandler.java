package com.movierang.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException e, HttpServletRequest request) {
        log.warn("영화를 찾을 수 없음: {}, URI: {}", e.getMessage(), request.getRequestURI());


        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e, HttpServletRequest request) {
        log.warn("잘못된 요청: {}, URI: {}", e.getMessage(), request.getRequestURI());

        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException e, HttpServletRequest request) {
        log.error("외부 API 호출 실패: {}, URI: {}", e.getMessage(), request.getRequestURI());

        ErrorResponse error = new ErrorResponse(
                "EXTERNAL_API_ERROR",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e, HttpServletRequest request) {
        log.warn("인증되지 않은 요청: {}, URI: {}", e.getMessage(), request.getRequestURI());

        ErrorResponse error = new ErrorResponse(
                "UNAUTHORIZED",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 에러 발생, URI: {}", request.getRequestURI(), e);

        ErrorResponse error = new ErrorResponse(
                "ERROR",
                e.getMessage(),
                request.getRequestURI()   // ← path 추가됨
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
