package com.example.demo.init;

import com.example.demo.entity.MovieEntity;
import com.example.demo.repository.MovieRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*@Slf4j
@Profile("dev") // ✅ 개발 환경에서만 실행
//@Component
@RequiredArgsConstructor
public class MovieCsvInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final MovieDetailRepository movieDetailRepository;

    @Override
    public void run(String... args) throws Exception {
        if (movieRepository.count() > 0) {
            log.info("🎬 기존 Movie 데이터가 존재하므로 CSV 로드를 건너뜁니다.");
            return;
        }

        log.info("🚀 KMDB CSV 로드 시작...");

        var inputStream = getClass().getResourceAsStream("/data/KMDB_2025.csv");
        if (inputStream == null) {
            throw new IllegalStateException("CSV 파일을 찾을 수 없습니다! (/resources/data 경로 확인)");
        }

        List<MovieEntity> movieEntities = new ArrayList<>();
        List<MovieDetailEntity> detailEntities = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext(); // ✅ 헤더 스킵
            String[] line;
            int lineCount = 0;

            while ((line = reader.readNext()) != null) {
                // CSV 열 순서에 맞게 매핑
                String title = safe(line, 0);
                String prodYear = safe(line, 2);
                String nation = safe(line, 5);
                String genre = safe(line, 10);
                String poster = safe(line, 17);

                MovieEntity movie = MovieEntity.builder()
                        .title(title)
                        .prodYear(prodYear)
                        .nation(nation)
                        .genre(genre)
                        .poster(poster)
                        .rating(safe(line, 9))
                        .repRlsDate(safe(line, 14).replace("|", ""))
                        .build();

                movieEntities.add(movie);

                MovieDetailEntity detail = MovieDetailEntity.builder()
                        .movie(movie)
                        .plot(safe(line, 7))
                        .runtime(safe(line, 8))
                        .type(safe(line, 12))
                        .useType(safe(line, 13))
                        .kmdbUrl(safe(line, 11))
                        .posters(safe(line, 17))
                        .stills(safe(line, 18))
                        .vodUrl(safe(line, 19))
                        .modDate(safe(line, 22))
                        .build();

                detailEntities.add(detail);

                lineCount++;
                if (lineCount % 200 == 0) {
                    log.info("📦 {}편 로드 중...", lineCount);
                }
            }

            // ✅ Batch insert
            movieRepository.saveAll(movieEntities);
            movieDetailRepository.saveAll(detailEntities);

            log.info("✅ CSV 로드 완료! 총 {}편 저장됨", movieEntities.size());
        } catch (Exception e) {
            log.error("❌ CSV 로드 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }



    private String safe(String[] line, int index) {
        return (index < line.length && line[index] != null) ? line[index].trim() : null;
    }
}*/