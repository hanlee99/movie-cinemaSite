package com.movierang.oauth;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // 네이버는 response 안에 실제 데이터가 있음
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "NAVER";
    }

    @Override
    public String getOauthId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getNickname() {
        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("nickname");
    }
}
