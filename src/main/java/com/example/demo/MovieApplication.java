package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling   // 스케줄링 활성화
@EnableJpaAuditing
public class MovieApplication {

	public static void main(String[] args) {
        SpringApplication.run(MovieApplication.class, args);
	}

}
