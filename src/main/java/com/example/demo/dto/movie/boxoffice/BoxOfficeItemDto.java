package com.example.demo.dto.movie.boxoffice;

import com.example.demo.external.kobis.KobisDailyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
public class BoxOfficeItemDto {
    private int rank;
    private String movieCd;
    private String title;
    private LocalDate openDt;
    private Long salesAmt;
    private Long salesAcc;
    private Long audiCnt;
    private Long audiAcc;

    public static BoxOfficeItemDto from(KobisDailyResponse.DailyBoxOfficeItem item) {
        return BoxOfficeItemDto.builder()
                .rank(Integer.parseInt(item.getRank()))
                .movieCd(item.getMovieCd())
                .title(item.getMovieNm())
                .openDt(parseDate(item.getOpenDt()))
                .salesAmt(Long.parseLong(item.getSalesAmt()))
                .salesAcc(Long.parseLong(item.getSalesAcc()))
                .audiCnt(Long.parseLong(item.getAudiCnt()))
                .audiAcc(Long.parseLong(item.getAudiAcc()))
                .build();
    }

    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
    }
}

