package com.example.demo.external.adapter;

import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.external.kobis.KobisApiClient;
import com.example.demo.external.kobis.KobisDailyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KobisAdapter {
    private final KobisApiClient kobisApiClient;

    public DailyBoxOfficeResultDto getDailyBoxOffice(String date) {
        try {
            KobisDailyResponse response = kobisApiClient.getDailyBoxOffice(date);
            if (response == null) {
                throw new RuntimeException("KOBIS 응답이 비어있습니다.");
            }
            return DailyBoxOfficeResultDto.from(response);  // ✅ 직접 호출
        } catch (Exception e) {
            throw new RuntimeException("박스오피스 조회 실패: " + e.getMessage(), e);
        }
    }
}
