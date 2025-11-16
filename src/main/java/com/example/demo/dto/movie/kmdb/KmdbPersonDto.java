package com.example.demo.dto.movie.kmdb;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KmdbPersonDto {
    private String personId;     // KMDB personId (nullable)
    private String name;         // 인물명 (예: 하나에 나츠키)
    private String nameEn;       // 영문명 (있을 수도 없을 수도)
    private String roleGroup;    // 역할 그룹 (감독 / 출연 / 원작)
    private String roleName;     // 세부 역할명 (배역명 또는 직함)
}
