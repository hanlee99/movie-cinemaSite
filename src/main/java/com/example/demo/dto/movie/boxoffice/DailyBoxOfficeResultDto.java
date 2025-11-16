package com.example.demo.dto.movie.boxoffice;

import com.example.demo.external.kobis.KobisDailyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DailyBoxOfficeResultDto {
    private String boxOfficeType;
    private String showRange;
    private List<BoxOfficeItemDto> dailyBoxOfficeList;

    public static DailyBoxOfficeResultDto from(KobisDailyResponse response) {
        var result = response.getBoxOfficeResult();

        // 원래 showRange 예: "20250101~20250107"
        String rawRange = result.getShowRange(); // "YYYYMMDD~YYYYMMDD"

        // "~" 기준으로 split → 앞 날짜만 사용
        String startDate = rawRange.split("~")[0]; // "20250101"

        // 보기 좋게 하려면 YYYY-MM-DD 로 포맷팅
        String formatted = startDate.substring(0, 4) + "-"
                + startDate.substring(4, 6) + "-"
                + startDate.substring(6, 8);

        return DailyBoxOfficeResultDto.builder()
                .boxOfficeType(result.getBoxofficeType())
                .showRange(formatted) // 여기로 적용
                .dailyBoxOfficeList(result.getDailyBoxOfficeList().stream()
                        .map(BoxOfficeItemDto::from)
                        .toList())
                .build();
    }
}
