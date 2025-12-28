package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling   // 스케줄링 활성화
@EnableJpaAuditing
@EnableRetry  // Retry 기능 활성화 (동시성 제어용)
public class MovieApplication {

	public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);
	}

}
