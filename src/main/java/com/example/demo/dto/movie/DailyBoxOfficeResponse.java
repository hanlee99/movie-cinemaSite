package com.example.demo.dto.movie;

import com.example.demo.dto.movie.boxoffice.BoxOfficeItemDto;
import com.example.demo.dto.movie.boxoffice.DailyBoxOfficeResultDto;
import com.example.demo.entity.MovieEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DailyBoxOfficeResponse {
    private String boxOfficeType;
    private String showRange;
    private List<Movie> movies;

    public static DailyBoxOfficeResponse from(
            DailyBoxOfficeResultDto boxOffice,
            Map<String, MovieEntity> movieMap) {

        List<Movie> movies = boxOffice.getDailyBoxOfficeList().stream()
                .map(item -> {
                    MovieEntity movie = movieMap.get(item.getTitle());
                    return Movie.from(item, movie);
                })
                .toList();

        return DailyBoxOfficeResponse.builder()
                .boxOfficeType(boxOffice.getBoxOfficeType())
                .showRange(boxOffice.getShowRange())
                .movies(movies)
                .build();
    }

    @Getter
    @Builder
    public static class Movie {
        private int rank;
        private String movieCd;
        private String docId;
        private String title;
        private String poster;
        private String genre;
        private LocalDate openDt;
        private Long salesAmt;
        private Long salesAcc;
        private Long audiCnt;
        private Long audiAcc;

        public static Movie from(BoxOfficeItemDto boxOffice, MovieEntity movie) {
            return Movie.builder()
                    .rank(boxOffice.getRank())
                    .movieCd(boxOffice.getMovieCd())
                    .docId(movie != null ? movie.getDocId() : null)
                    .title(boxOffice.getTitle())
                    .openDt(boxOffice.getOpenDt())
                    .salesAmt(boxOffice.getSalesAmt())
                    .salesAcc(boxOffice.getSalesAcc())
                    .audiCnt(boxOffice.getAudiCnt())
                    .audiAcc(boxOffice.getAudiAcc())
                    .poster(movie != null ? movie.getPoster() : null)
                    .genre(movie != null ? movie.getGenre() : null)
                    .build();
        }
    }
}
