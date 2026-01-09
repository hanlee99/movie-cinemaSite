package com.movierang.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String email;
    private String nickname;
}
