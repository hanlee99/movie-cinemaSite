package com.movierang.entity;

import com.movierang.dto.movie.kmdb.KmdbMovieDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "movie")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // KOBIS/KMDB 식별자
    private String movieId;
    private String movieSeq;
    private String docId;

    // 기본 정보
    private String title;
    private String titleEng;
    private String titleOrg;
    @Column(length = 500)
    private String titleEtc;
    private String prodYear;
    private String nation;
    private String company;
    private String genre;
    private String rating;
    private String repRlsDate;
    private String runtime;

    // 상세 정보 (TEXT 타입)
    @Column(columnDefinition = "TEXT")
    private String plot;

    private String type;
    private String useType;
    private String kmdbUrl;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    private String modDate;

    // 이미지/미디어 (TEXT 타입)
    private String poster;

    @Column(columnDefinition = "TEXT")
    private String posters;      // | 구분자로 저장

    @Column(columnDefinition = "TEXT")
    private String stills;       // | 구분자로 저장

    @Column(columnDefinition = "TEXT")
    private String vodUrls;      // | 구분자로 저장

    // 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MoviePersonEntity> people = new ArrayList<>();

    // ✅ KMDB DTO → Entity 변환
    public static MovieEntity from(KmdbMovieDto dto) {
        return MovieEntity.builder()
                .movieId(dto.getMovieId())
                .movieSeq(dto.getMovieSeq())
                .docId(dto.getDocId())
                .title(dto.getTitle() != null ? dto.getTitle().trim() : null)
                .titleEng(dto.getTitleEng() != null ? dto.getTitleEng().trim() : null)
                .titleOrg(dto.getTitleOrg() != null ? dto.getTitleOrg().trim() : null)
                .titleEtc(dto.getTitleEtc() != null ? dto.getTitleEtc().trim() : null)
                .prodYear(dto.getProdYear())
                .nation(dto.getNation())
                .company(dto.getCompany())
                .genre(dto.getGenre())
                .rating(dto.getRating())
                .repRlsDate(dto.getRepRlsDate())
                .runtime(dto.getRuntime())
                .plot(dto.getPlot())
                .type(dto.getType())
                .useType(dto.getUseType())
                .kmdbUrl(dto.getKmdbUrl())
                .keywords(dto.getKeywords())
                .modDate(dto.getModDate())
                .poster(dto.getPoster())
                .posters(joinWithPipe(dto.getPosters()))      // List → String
                .stills(joinWithPipe(dto.getStills()))        // List → String
                .vodUrls(joinWithPipe(dto.getVodUrls()))      // List → String
                .build();
    }

    // List<String> → "url1|url2|url3" 변환
    private static String joinWithPipe(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return null;
        }
        return String.join("|", urls);
    }

    // 편의 메서드
    public void addPerson(MoviePersonEntity person) {
        people.add(person);
        person.assignMovie(this);
    }
}
