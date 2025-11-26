package com.example.demo.mapper;

import com.example.demo.dto.movie.kmdb.KmdbMovieDto;
import com.example.demo.dto.movie.kmdb.KmdbPersonDto;
import com.example.demo.external.kmdb.KmdbResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class KmdbMovieMapper {

    public KmdbMovieDto toDto(KmdbResponse.Result result) {
        return KmdbMovieDto.builder()
                // 식별자
                .movieId(result.getMovieId())
                .movieSeq(result.getMovieSeq())
                .docId(result.getDOCID())

                // 기본 정보
                .title(result.getTitle())
                .titleEng(result.getTitleEng())
                .titleOrg(result.getTitleOrg())
                .titleEtc(result.getTitleEtc())
                .prodYear(result.getProdYear())
                .nation(result.getNation())
                .company(result.getCompany())
                .genre(result.getGenre())
                .rating(result.getRating())
                .repRlsDate(result.getRepRlsDate())
                .runtime(result.getRuntime())

                // 상세 정보
                .plot(extractPlot(result.getPlots()))
                .type(result.getType())
                .useType(result.getUse())
                .kmdbUrl(result.getKmdbUrl())
                .keywords(result.getKeywords())
                .modDate(result.getModDate())

                // 이미지/미디어
                .poster(extractFirstPoster(result.getPosters()))
                .posters(splitUrls(result.getPosters()))
                .stills(splitUrls(result.getStlls()))
                .vodUrls(extractVodUrls(result.getVods()))

                // 인물
                .staffs(extractStaffs(result.getStaffs()))
                .build();
    }

    // ========== 포스터/스틸 ==========
    private String extractFirstPoster(String posters) {
        if (posters == null || posters.isBlank()) return null;
        return posters.split("\\|")[0];
    }

    private List<String> splitUrls(String urls) {
        if (urls == null || urls.isBlank()) return List.of();
        return Arrays.stream(urls.split("\\|"))
                .filter(s -> !s.isBlank())
                .toList();
    }

    // ========== VOD ==========
    private List<String> extractVodUrls(KmdbResponse.Vods vods) {
        if (vods == null || vods.getVod() == null) return List.of();

        return vods.getVod().stream()
                .map(KmdbResponse.Vod::getVodUrl)
                .filter(Objects::nonNull)
                .toList();
    }

    // ========== 줄거리 ==========
    private String extractPlot(KmdbResponse.Plots plots) {
        if (plots == null || plots.getPlot() == null || plots.getPlot().isEmpty()) {
            return null;
        }

        // 한국어 줄거리 우선
        return plots.getPlot().stream()
                .filter(p -> "한국어".equals(p.getPlotLang()))
                .map(KmdbResponse.Plot::getPlotText)
                .findFirst()
                .orElseGet(() -> plots.getPlot().get(0).getPlotText());
    }

    // ========== 인물 ==========
    private List<KmdbPersonDto> extractStaffs(KmdbResponse.Staffs staffs) {
        if (staffs == null || staffs.getStaff() == null) return List.of();

        // 감독/원작
        List<KmdbPersonDto> directors = staffs.getStaff().stream()
                .filter(s -> "감독".equals(s.getStaffRoleGroup())
                        || "원작".equals(s.getStaffRoleGroup()))
                .map(this::toPersonDto)
                .toList();

        // 출연진 상위 15명
        List<KmdbPersonDto> actors = staffs.getStaff().stream()
                .filter(s -> "출연".equals(s.getStaffRoleGroup()))
                .limit(30)  // 상위 15명만
                .map(this::toPersonDto)
                .toList();

        // 합치기
        return Stream.concat(directors.stream(), actors.stream())
                .toList();
    }

    private KmdbPersonDto toPersonDto(KmdbResponse.Staff staff) {
        return KmdbPersonDto.builder()
                .personId(nullIfBlank(staff.getStaffId()))
                .name(staff.getStaffNm())
                .nameEn(staff.getStaffEnNm())
                .roleGroup(staff.getStaffRoleGroup())
                .roleName(staff.getStaffRole())
                .build();
    }

    private String nullIfBlank(String str) {
        return (str == null || str.isBlank()) ? null : str;
    }
}
