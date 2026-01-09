package com.movierang.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ViewExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    public String handleMovieNotFound(MovieNotFoundException e, HttpServletRequest request, Model model) {
        log.warn("영화를 찾을 수 없음: {}, URI: {}", e.getMessage(), request.getRequestURI());

        model.addAttribute("errorCode", "NOT_FOUND");
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException e, HttpServletRequest request, Model model) {
        log.warn("잘못된 요청: {}, URI: {}", e.getMessage(), request.getRequestURI());

        model.addAttribute("errorCode", "BAD_REQUEST");
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/400";
    }

    @ExceptionHandler(ExternalApiException.class)
    public String handleExternalApi(ExternalApiException e, HttpServletRequest request, Model model) {
        log.error("외부 API 호출 실패: {}, URI: {}", e.getMessage(), request.getRequestURI());

        model.addAttribute("errorCode", "EXTERNAL_API_ERROR");
        model.addAttribute("errorMessage", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        model.addAttribute("path", request.getRequestURI());

        return "error/500";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException e, HttpServletRequest request, Model model) {
        log.warn("인증되지 않은 요청: {}, URI: {}", e.getMessage(), request.getRequestURI());

        model.addAttribute("errorCode", "UNAUTHORIZED");
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleDefault(Exception e, HttpServletRequest request, Model model) {
        log.error("예상치 못한 에러 발생, URI: {}", request.getRequestURI(), e);

        model.addAttribute("errorCode", "INTERNAL_SERVER_ERROR");
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        model.addAttribute("path", request.getRequestURI());

        return "error/500";
    }
}
