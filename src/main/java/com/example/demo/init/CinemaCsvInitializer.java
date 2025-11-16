package com.example.demo.init;

import com.example.demo.entity.BrandEntity;
import com.example.demo.entity.CinemaEntity;
import com.example.demo.entity.RegionEntity;
import com.example.demo.entity.SpecialtyTheaterEntity;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CinemaRepository;
import com.example.demo.repository.RegionRepository;
import com.example.demo.repository.SpecialtyTheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.math.BigDecimal;

//@Component
@Profile("local")  // local 프로파일에서만 실행
@RequiredArgsConstructor
public class CinemaCsvInitializer  {

    /*private final BrandRepository brandRepository;
    private final RegionRepository regionRepository;
    private final CinemaRepository cinemaRepository;
    private final SpecialtyTheaterRepository specialtyTheaterRepository;
implements CommandLineRunner
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (cinemaRepository.count() > 0) {
            System.out.println("🎦 기존 Cinema 데이터가 존재하므로 CSV 로드를 건너뜁니다.");
            return;
        }
        System.out.println("CSV 데이터 로드 시작...");

        // 1️⃣ 조인 테이블(관계) 먼저 삭제 — JPA 방식
        cinemaRepository.findAll()
                .forEach(cinema -> cinema.getSpecialtyTheaterEntities().clear());

        // 2️⃣ 관련 엔티티 순서대로 삭제 (외래키 역순)
        specialtyTheaterRepository.deleteAll();
        cinemaRepository.deleteAll();
        regionRepository.deleteAll();
        brandRepository.deleteAll();

        var inputStream = getClass().getResourceAsStream("/data/cinema-data.csv");
        if (inputStream == null) {
            throw new IllegalStateException("CSV 파일을 찾을 수 없습니다! (경로 확인: /data/cinema-data.csv)");
        }
        System.out.println("CSV 파일 로드 성공!");

        // resources 폴더의 CSV 파일 읽기
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/data/cinema-data.csv"),
                        StandardCharsets.UTF_8
                ))) {

            reader.readNext(); // 첫 줄(헤더) 건너뛰기
            String[] parts;
            while ((parts = reader.readNext()) != null) {
                if (parts.length < 10) continue;

                String cinemaName = parts[0].trim();
                String brandName = parts[1].trim();
                String businessStatus = parts[2].trim();
                String classificationRegion = parts[3].trim();
                String regionalLocal = parts[4].trim();
                String basicLocal = parts[5].trim();
                String streetAddress = parts[6].trim();
                String loadAddress = parts[7].trim();
                String xStr = parts[8].replaceAll("[^0-9.]", "").trim(); // x
                String yStr = parts[9].replaceAll("[^0-9.]", "").trim(); // y

                if (xStr.isEmpty() || yStr.isEmpty()) {
                    System.out.println("좌표 누락: " + cinemaName + " → 스킵");
                    continue;
                }

                BigDecimal longitude = new BigDecimal(xStr);
                BigDecimal latitude = new BigDecimal(yStr);

                // 브랜드 중복 체크 후 저장
                BrandEntity brandEntity = brandRepository.findAll()
                        .stream()
                        .filter(b -> b.getName().equals(brandName))
                        .findFirst()
                        .orElseGet(() -> brandRepository.save(new BrandEntity(null, brandName)));

                // 지역 중복 체크 후 저장
                RegionEntity regionEntity = regionRepository.findAll()
                        .stream()
                        .filter(r -> r.getRegionalLocal().equals(regionalLocal) && r.getBasicLocal().equals(basicLocal))
                        .findFirst()
                        .orElseGet(() -> regionRepository.save(new RegionEntity(null, regionalLocal, basicLocal)));

                // 영화관 저장
                CinemaEntity cinemaEntity = new CinemaEntity();
                cinemaEntity.setCinemaName(cinemaName);
                cinemaEntity.setBusinessStatus(businessStatus);
                cinemaEntity.setClassificationRegion(classificationRegion);
                cinemaEntity.setStreetAddress(streetAddress);
                cinemaEntity.setLoadAddress(loadAddress);
                cinemaEntity.setLatitude(latitude);
                cinemaEntity.setLongitude(longitude);
                cinemaEntity.setBrandEntity(brandEntity);
                cinemaEntity.setRegionEntity(regionEntity);
                cinemaRepository.save(cinemaEntity);
            }
        }
        System.out.println("CSV 데이터 500행 로드 완료!");

        System.out.println("🎬 특별관 CSV 로드 시작...");

        var specialStream = getClass().getResourceAsStream("/data/specialty-theater.csv");
        if (specialStream == null) {
            System.out.println("⚠️ specialty-theater.csv 파일이 없습니다. 건너뜁니다.");
            return;
        }

        try (CSVReader specialReader = new CSVReader(
                new InputStreamReader(specialStream, StandardCharsets.UTF_8))) {

            specialReader.readNext(); // 헤더 건너뛰기
            String[] row;

            while ((row = specialReader.readNext()) != null) {
                if (row.length < 3) continue;

                String brandName = row[0].trim();
                String specialtyName = row[1].trim();
                String cinemaName = row[2].trim();

                // 브랜드 찾기
                BrandEntity brandEntity = brandRepository.findByName(brandName)
                        .orElseThrow(() -> new IllegalStateException("❌ 브랜드 없음: " + brandName));

                // 특별관 찾기 or 생성
                SpecialtyTheaterEntity theater = specialtyTheaterRepository
                        .findByNameAndBrand_Name(specialtyName, brandName)
                        .orElseGet(() -> specialtyTheaterRepository.save(
                                SpecialtyTheaterEntity.builder()
                                        .name(specialtyName)
                                        .brandEntity(brandEntity)
                                        .build()
                        ));

                // 영화관 찾기 후 연결
                cinemaRepository.findByCinemaNameAndBrand_Name(cinemaName, brandName)
                        .ifPresentOrElse((CinemaEntity cinemaEntity) -> {
                            cinemaEntity.getSpecialtyTheaterEntities().add(theater);
                            cinemaRepository.save(cinemaEntity);
                            System.out.printf("✅ [%s - %s] 연결 완료%n", specialtyName, cinemaName);
                        }, () -> System.out.printf("⚠️ 영화관 [%s] 을 찾을 수 없습니다.%n", cinemaName));
            }
        }
        System.out.println("🎉 특별관 CSV 데이터 로드 완료!");
    }*/
}

