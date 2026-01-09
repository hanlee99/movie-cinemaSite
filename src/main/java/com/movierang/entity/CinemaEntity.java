package com.movierang.entity;

import com.movierang.dto.cinema.CinemaResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "cinema", indexes = {
        @Index(name = "idx_cinema_coordinates", columnList = "x_epsg5174, y_epsg5174"),
        @Index(name = "idx_cinema_brand", columnList = "brand_id")
})
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
    private String classificationRegion;

    @Column(length = 255)
    private String streetAddress;

    @Column(length = 255)
    private String loadAddress;

    @Column(name = "x_epsg5174", precision = 20, scale = 10)
    private BigDecimal xEpsg5174;

    @Column(name = "y_epsg5174", precision = 20, scale = 10)
    private BigDecimal yEpsg5174;

    @Column(name = "lat_wgs84", precision = 20, scale = 10)
    private BigDecimal latWgs84;

    @Column(name = "lon_wgs84", precision = 20, scale = 10)
    private BigDecimal lonWgs84;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brandEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity regionEntity;

    @ManyToMany
    @JoinTable(
            name = "cinema_specialtytheater",
            joinColumns = @JoinColumn(name = "cinema_id"),
            inverseJoinColumns = @JoinColumn(name = "specialtytheater_id")
    )
    private Set<SpecialtyTheaterEntity> specialtyTheaterEntities = new HashSet<>();

    // CinemaEntity에 추가
    public CinemaResponseDto toDto() {
        return CinemaResponseDto.builder()
                .cinemaId(this.cinemaId)
                .cinemaName(this.cinemaName)
                .brand(this.brandEntity != null ? this.brandEntity.getName() : null)
                .region(this.regionEntity != null ? this.regionEntity.getBasicLocal() : null)
                .classificationRegion(this.classificationRegion)
                .streetAddress(this.streetAddress)
                .loadAddress(this.loadAddress)
                .latitude(this.latWgs84)
                .longitude(this.lonWgs84)
                .specialtyTheaters(
                        this.specialtyTheaterEntities.stream()
                                .map(SpecialtyTheaterEntity::getName)
                                .collect(Collectors.toList())
                ).build();
    }
}
