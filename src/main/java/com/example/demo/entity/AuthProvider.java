package com.example.demo.entity;

public enum AuthProvider {
    LOCAL("일반 가입"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GOOGLE("구글");

    private final String displayName;

    AuthProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
