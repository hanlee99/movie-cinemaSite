package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching      // 캐시 활성화
@EnableScheduling   // 스케줄링 활성화
public class MovieApplication {

	public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);
	}

}
