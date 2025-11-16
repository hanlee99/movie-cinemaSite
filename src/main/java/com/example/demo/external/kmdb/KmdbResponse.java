package com.example.demo.external.kmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class KmdbResponse {
    private String Query;
    private String KMAQuery;
    private int TotalCount;
    private List<Data> Data;

    // ------------------------------
    // ✅ 1) Data Wrapper
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Data {
        private String CollName;
        private int TotalCount;
        private int Count;
        private List<Result> Result;
    }

    // ------------------------------
    // ✅ 2) Result (영화 정보)
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Result {
        private String DOCID;
        private String movieId;
        private String movieSeq;

        private String title;
        private String titleEng;
        private String titleOrg;
        private String titleEtc;

        private String prodYear;
        private String nation;
        private String company;

        private Plots plots;
        private String runtime;
        private String rating;
        private String genre;
        private String kmdbUrl;
        private String type;
        private String use;
        private String repRlsDate;
        private String keywords;

        private String posters;
        private String stlls;

        private Vods vods;
        private Directors directors;
        private Actors actors;
        private Staffs staffs;

        private String modDate;
    }

    // ------------------------------
    // ✅ 3) 줄거리
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Plots {
        private List<Plot> plot;
    }

    @Getter @Setter
    public static class Plot {
        private String plotLang;
        private String plotText;
    }

    // ------------------------------
    // ✅ 4) 감독
    // ------------------------------
    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Directors {
        private List<Director> director;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Director {
        private String directorNm;
        private String directorEnNm;
        private String directorId;
    }

    // ------------------------------
    // ✅ 5) 배우
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Actors {
        private List<Actor> actor;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Actor {
        private String actorNm;
        private String actorEnNm;
        private String actorId;
    }

    // ------------------------------
    // ✅ 6) 스태프
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Staffs {
        private List<Staff> staff;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Staff {
        private String staffNm;
        private String staffEnNm;
        private String staffRoleGroup;
        private String staffRole;
        private String staffEtc;
        private String staffId;
    }

    // ------------------------------
    // ✅ 7) VOD
    // ------------------------------
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Vods {
        private List<Vod> vod;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Vod {
        private String vodClass;
        private String vodUrl;
    }
}
