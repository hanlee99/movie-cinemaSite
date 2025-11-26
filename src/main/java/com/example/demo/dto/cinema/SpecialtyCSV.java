package com.example.demo.dto.cinema;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyCSV {
    @CsvBindByName(column = "브랜드")
    private String brandName;

    @CsvBindByName(column = "특별관이름")
    private String specialtyName;

    @CsvBindByName(column = "영화관이름")
    private String cinemaName;
}