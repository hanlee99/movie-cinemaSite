package com.example.demo.external.kobis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KobisDailyResponse {
    private BoxOfficeResult boxOfficeResult;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxOfficeResult{
        private String boxofficeType;
        private String showRange;
        private List<DailyBoxOfficeItem> dailyBoxOfficeList;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyBoxOfficeItem {
        private String rank;
        private String movieCd;
        private String movieNm;
        private String openDt;
        private String salesAmt;
        private String salesAcc;
        private String audiCnt;
        private String audiAcc;
    }
}
