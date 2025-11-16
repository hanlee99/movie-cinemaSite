package com.example.demo.dto.movie.kmdb;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KmdbMovieDto {

    // 식별자
    private String movieId;
    private String movieSeq;
    private String docId;

    // 기본 정보
    private String title;
    private String titleEng;
    private String titleOrg;      // 원제
    private String titleEtc;      // 기타 제명
    private String prodYear;
    private String nation;
    private String company;
    private String genre;
    private String rating;
    private String repRlsDate;
    private String runtime;

    // 상세 정보 (TEXT 필드지만 @Column 제거)
    private String plot;          // DTO에는 어노테이션 불필요
    private String type;
    private String useType;
    private String kmdbUrl;
    private String keywords;
    private String modDate;

    // 이미지 및 미디어
    private String poster;                // 대표 포스터
    private List<String> posters;         // 전체 포스터 목록
    private List<String> stills;          // 스틸컷 목록
    private List<String> vodUrls;         // VOD URL 목록

    // 인물
    private List<KmdbPersonDto> staffs;
}
