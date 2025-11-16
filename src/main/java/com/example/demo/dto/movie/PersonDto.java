package com.example.demo.dto.movie;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDto {
    private Long id;
    private String name;
    private String nameEn;
    private String roleGroup; // ex. "감독", "배우"
    private String roleName;  // ex. "주연", "조연"
}
