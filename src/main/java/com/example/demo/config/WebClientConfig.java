package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kobisWebClient() {
        return WebClient.builder()
                .baseUrl("https://kobis.or.kr/kobisopenapi/webservice/rest")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)) // ✅ 16MB까지 허용
                        .build())
                .build();
    }

    @Bean
    public WebClient kmdbWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)) // ✅ 16MB까지 허용
                        .build())
                .build();
    }
}
