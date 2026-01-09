package com.movierang.oauth;

public interface OAuth2UserInfo {
    String getProvider();      // "NAVER", "GOOGLE"
    String getOauthId();       // Provider의 unique ID
    String getEmail();
    String getNickname();
}
