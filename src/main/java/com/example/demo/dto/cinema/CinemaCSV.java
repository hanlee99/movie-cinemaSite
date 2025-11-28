package com.example.demo.dto.cinema;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaCSV {
    @CsvBindByName(column = "영화관명")
    private String cinemaName;

    @CsvBindByName(column = "브랜드명")
    private String brandName;

    @CsvBindByName(column = "영업상태명")
    private String businessStatus;

    @CsvBindByName(column = "영화관지역명")
    private String classificationRegion;

    @CsvBindByName(column = "광역단체")
    private String regionalLocal;

    @CsvBindByName(column = "기초단체")
    private String basicLocal;

    @CsvBindByName(column = "번지명")
    private String loadAddress;

    @CsvBindByName(column = "도로명")
    private String streetAddress;

    @CsvBindByName(column = "x(epsg5174)")
    private Double xEpsg5174;

    @CsvBindByName(column = "y(epsg5174)")
    private Double yEpsg5174;

    @CsvBindByName(column = "lat_wgs84")
    private Double latWgs84;

    @CsvBindByName(column = "lon_wgs84")
    private Double lonWgs84;
}
