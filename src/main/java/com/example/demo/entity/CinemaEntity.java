package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cinemaId;

    @Column(length = 100)
    private String cinemaName;

    @Column(length = 20)
    private String businessStatus;

    @Column(length = 50)
    private String classificationRegion; // ex. 서울 (브랜드 기준 지역명)

    @Column(length = 255)
    private String streetAddress;

    @Column(length = 255)
    private String loadAddress;

    @Column(precision = 15, scale = 8)
    private BigDecimal longitude;

    @Column(precision = 15, scale = 8)
    private BigDecimal latitude;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brandEntity;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private RegionEntity regionEntity;

    @ManyToMany
    @JoinTable(
            name = "cinema_specialtytheater",  // ✅ 테이블명 통일
            joinColumns = @JoinColumn(name = "cinema_id"),
            inverseJoinColumns = @JoinColumn(name = "specialtytheater_id") // ✅ 컬럼명 통일
    )
    private Set<SpecialtyTheaterEntity> specialtyTheaterEntities = new HashSet<>();


}
