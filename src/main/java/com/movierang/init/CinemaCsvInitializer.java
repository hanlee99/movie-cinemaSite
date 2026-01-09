package com.movierang.init;

import com.movierang.dto.cinema.CinemaCSV;
import com.movierang.dto.cinema.SpecialtyCSV;
import com.movierang.entity.BrandEntity;
import com.movierang.entity.CinemaEntity;
import com.movierang.entity.RegionEntity;
import com.movierang.entity.SpecialtyTheaterEntity;
import com.movierang.repository.BrandRepository;
import com.movierang.repository.CinemaRepository;
import com.movierang.repository.RegionRepository;
import com.movierang.repository.SpecialtyTheaterRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
//@Profile("local")
@RequiredArgsConstructor
public class CinemaCsvInitializer implements ApplicationRunner {

    private final CinemaRepository cinemaRepository;
    private final BrandRepository brandRepository;
    private final RegionRepository regionRepository;
    private final SpecialtyTheaterRepository specialtyTheaterRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (cinemaRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다.");
            return;
        }

        try {
            initBrands();
            initRegions();
            initCinemas();
            initSpecialtyMappings();

            log.info("모든 데이터 초기화 완료!");

        } catch (Exception e) {
            log.error("초기화 실패", e);
        }
    }

    private void initBrands() {
        if (brandRepository.count() == 0) {
            brandRepository.save(new BrandEntity(null, "CGV"));
            brandRepository.save(new BrandEntity(null, "롯데시네마"));
            brandRepository.save(new BrandEntity(null, "메가박스"));
            log.info("브랜드 3개 저장 완료");
        }
    }

    private void initRegions() throws Exception {
        log.info("지역 데이터 생성 중...");
        ClassPathResource resource = new ClassPathResource("datafile/cinema_data.csv");
        InputStream inputStream = resource.getInputStream();

        List<CinemaCSV> csvList = new CsvToBeanBuilder<CinemaCSV>(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withType(CinemaCSV.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build().parse();

        // 중복 제거하고 Region 생성
        Set<String> regionSet = new HashSet<>();
        for (CinemaCSV csv : csvList) {
            if (csv.getRegionalLocal() != null && csv.getBasicLocal() != null) {
                String key = csv.getRegionalLocal() + "|" + csv.getBasicLocal();
                regionSet.add(key);
            }
        }

        int savedCount = 0;
        for (String regionKey : regionSet) {
            String[] parts = regionKey.split("\\|");
            RegionEntity region = new RegionEntity();
            region.setRegionalLocal(parts[0]);
            region.setBasicLocal(parts[1]);
            regionRepository.save(region);
            savedCount++;
        }

        log.info("지역 {}개 저장 완료", savedCount);
    }

    private void initCinemas() throws Exception {
        log.info("영화관 데이터 로딩 중...");
        ClassPathResource resource = new ClassPathResource("datafile/cinema_data.csv");
        InputStream inputStream = resource.getInputStream();

        List<CinemaCSV> csvList = new CsvToBeanBuilder<CinemaCSV>(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withType(CinemaCSV.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build().parse();

        log.info("CSV 파싱 완료: {}개", csvList.size());

        int savedCount = 0;

        for (CinemaCSV csv : csvList) {
            if (csv.getCinemaName() == null || csv.getCinemaName().trim().isEmpty()) {
                continue;
            }

            CinemaEntity cinema = new CinemaEntity();
            cinema.setCinemaName(csv.getCinemaName().trim());
            cinema.setBusinessStatus(csv.getBusinessStatus());

            cinema.setClassificationRegion(extractRegion(csv.getLoadAddress()));

            cinema.setLoadAddress(csv.getLoadAddress());
            cinema.setStreetAddress(csv.getStreetAddress());

            // EPSG:5174 좌표
            if (csv.getXEpsg5174() != null) {
                cinema.setXEpsg5174(BigDecimal.valueOf(csv.getXEpsg5174()));
            }
            if (csv.getYEpsg5174() != null) {
                cinema.setYEpsg5174(BigDecimal.valueOf(csv.getYEpsg5174()));
            }

            // WGS84 좌표
            if (csv.getLatWgs84() != null && csv.getLonWgs84() != null) {
                cinema.setLatWgs84(BigDecimal.valueOf(csv.getLatWgs84()));
                cinema.setLonWgs84(BigDecimal.valueOf(csv.getLonWgs84()));
                log.debug("WGS84 좌표 로드: {} ({}, {})",
                        csv.getCinemaName(), csv.getLatWgs84(), csv.getLonWgs84());
            } else {
                log.warn("WGS84 좌표 없음: {}", csv.getCinemaName());
            }

            // Brand 설정
            brandRepository.findByName(csv.getBrandName())
                    .ifPresent(cinema::setBrandEntity);

            // Region 설정
            if (csv.getRegionalLocal() != null && csv.getBasicLocal() != null) {
                regionRepository.findByRegionalLocalAndBasicLocal(
                        csv.getRegionalLocal(),
                        csv.getBasicLocal()
                ).ifPresent(cinema::setRegionEntity);
            }

            cinemaRepository.save(cinema);
            savedCount++;
        }

        log.info("영화관 {}개 저장 완료 (WGS84 좌표 포함)", savedCount);
    }

    private void initSpecialtyMappings() throws Exception {
        log.info("특별관 데이터 매핑 중...");
        ClassPathResource resource = new ClassPathResource("datafile/specialty_theater.csv");

        InputStream inputStream = resource.getInputStream();

        List<SpecialtyCSV> specialList = new CsvToBeanBuilder<SpecialtyCSV>(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .withType(SpecialtyCSV.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build().parse();

        log.info("특별관 CSV 파싱 완료: {}개", specialList.size());

        // 영화관 Map 생성
        Map<String, CinemaEntity> cinemaMap = cinemaRepository.findAllWithBrand().stream()
                .filter(c -> c.getBrandEntity() != null && c.getCinemaName() != null)
                .collect(Collectors.toMap(
                        c -> c.getBrandEntity().getName().trim() + "_" + c.getCinemaName().trim(),
                        c -> c,
                        (existing, replacement) -> existing
                ));

        log.info("DB에 {}개의 영화관 로드 완료", cinemaMap.size());

        int matchedCount = 0;
        int unmatchedCount = 0;

        for (SpecialtyCSV csv : specialList) {
            String brandName = csv.getBrandName() != null ? csv.getBrandName().trim() : "";
            String specialtyName = csv.getSpecialtyName() != null ? csv.getSpecialtyName().trim() : "";
            String cinemaName = csv.getCinemaName() != null ? csv.getCinemaName().trim() : "";

            if (brandName.isEmpty() || specialtyName.isEmpty() || cinemaName.isEmpty()) {
                log.warn("빈 데이터 스킵: brand={}, specialty={}, cinema={}",
                        brandName, specialtyName, cinemaName);
                continue;
            }

            // 특별관 생성/조회
            BrandEntity brand = brandRepository.findByName(brandName).orElse(null);

            SpecialtyTheaterEntity specialty = specialtyTheaterRepository
                    .findByName(specialtyName)
                    .orElseGet(() -> specialtyTheaterRepository.save(
                            SpecialtyTheaterEntity.builder()
                                    .name(specialtyName)
                                    .brandEntity(brand)
                                    .build()
                    ));

            // 영화관 찾기
            String key = brandName + "_" + cinemaName;
            CinemaEntity cinema = cinemaMap.get(key);

            if (cinema != null) {
                cinema.getSpecialtyTheaterEntities().add(specialty);
                matchedCount++;
            } else {
                unmatchedCount++;
                log.warn("매칭 실패: key='{}' (brand='{}', cinema='{}')",
                        key, brandName, cinemaName);
            }
        }

        cinemaRepository.saveAll(cinemaMap.values());

        log.info("특별관 매핑 완료! 성공: {}, 실패: {}", matchedCount, unmatchedCount);
    }

    private String extractRegion(String address) {
        if (address == null) return "기타";

        if (address.contains("서울")) return "서울";
        if (address.contains("경기")) return "경기";
        if (address.contains("인천")) return "인천";
        if (address.contains("대전")) return "대전";
        if (address.contains("충청")) return "충청";
        if (address.contains("세종")) return "세종";
        if (address.contains("대구광역시")) return "대구";
        if (address.contains("경상북도")) return "경북";
        if (address.contains("부산")) return "부산";
        if (address.contains("울산")) return "울산";
        if (address.contains("경상남도")) return "경남";
        if (address.contains("전라") || address.contains("전북")) return "전라";
        if (address.contains("광주")) return "광주";
        if (address.contains("강원")) return "강원";
        if (address.contains("제주")) return "제주";

        return "기타";
    }
}

