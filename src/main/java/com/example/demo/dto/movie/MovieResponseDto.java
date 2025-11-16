package com.example.demo.dto.movie;

import com.example.demo.entity.MovieEntity;
import com.example.demo.entity.MoviePersonEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDto {

    private String docId;
    private String title;
    private String titleEng;
    private String genre;
    private String rating;
    private String nation;
    private String prodYear;
    private String repRlsDate;
    private String poster;

    private String plot;
    private String runtime;
    private String company;
    private String type;
    private String useType;
    private String kmdbUrl;
    private String keywords;
    private List<String> posters;
    private List<String> stills;
    private List<String> vodUrls;
    private String modDate;

    private List<PersonDto> people;

    public static MovieResponseDto summary(MovieEntity entity) {
        return MovieResponseDto.builder()
                .docId(entity.getDocId())
                .title(entity.getTitle())
                .genre(entity.getGenre())
                .poster(entity.getPoster())
                .rating(entity.getRating())
                .repRlsDate(entity.getRepRlsDate())
                .build();
    }

    public static MovieResponseDto from(MovieEntity entity) {
        return MovieResponseDto.builder()
                .docId(entity.getDocId())
                .title(entity.getTitle())
                .titleEng(entity.getTitleEng())
                .genre(entity.getGenre())
                .rating(entity.getRating())
                .nation(entity.getNation())
                .prodYear(entity.getProdYear())
                .repRlsDate(entity.getRepRlsDate())
                .poster(entity.getPoster())
                .plot(entity.getPlot())
                .runtime(entity.getRuntime())
                .company(entity.getCompany())
                .type(entity.getType())
                .useType(entity.getUseType())
                .kmdbUrl(entity.getKmdbUrl())
                .keywords(entity.getKeywords())
                .posters(splitByPipe(entity.getPosters()))
                .stills(splitByPipe(entity.getStills()))
                .vodUrls(splitByPipe(entity.getVodUrls()))
                .modDate(entity.getModDate())
                .people(entity.getPeople().stream()
                        .map(PersonDto::from)
                        .toList())
                .build();
    }

    private static List<String> splitByPipe(String str) {
        if (str == null || str.isBlank()) return null;
        return Arrays.asList(str.split("\\|"));
    }

    // ✅ PersonDto
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonDto {
        private String name;
        private String nameEn;
        private String roleGroup;
        private String roleName;

        public static PersonDto from(MoviePersonEntity mp) {
            return PersonDto.builder()
                    .name(mp.getPerson().getName())
                    .nameEn(mp.getPerson().getNameEn())
                    .roleGroup(mp.getRoleGroup())
                    .roleName(mp.getRoleName())
                    .build();
        }
    }
}