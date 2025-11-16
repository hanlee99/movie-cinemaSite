package com.example.demo.external.kmdb;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KmdbRequest {
    // ✅ 상세 조회용 (둘 다 있으면 상세조회로 인식됨)
    private String movieId;
    private String movieSeq;

    // ✅ 검색용 기본 파라미터들
    private String query;      // 전체 검색어
    private String title;      // 제목
    private String director;   // 감독
    private String actor;      // 배우
    private String genre;      // 장르
    private String nation;     // 국가

    // ✅ 날짜 검색 (releaseDts / releaseDte)
    private String releaseDts;   // 개봉일 시작 YYYYMMDD
    private String releaseDte;   // 개봉일 끝   YYYYMMDD

    // ✅ 페이징
    private Integer listCount;
    private Integer startCount;

    // ✅ 상세 여부 (기본값)
    @Builder.Default
    private String detail = "N";
}
