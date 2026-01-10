package com.movierang.unit.mapper;

import com.movierang.dto.movie.kmdb.KmdbMovieDto;
import com.movierang.external.kmdb.KmdbResponse;
import com.movierang.mapper.KmdbMovieMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KmdbMovieMapper 단위 테스트
 * - KMDB 외부 DTO를 내부 DTO로 변환하는 로직 테스트
 */
@DisplayName("KmdbMovieMapper 단위 테스트")
class KmdbMovieMapperTest {
    private final KmdbMovieMapper mapper = new KmdbMovieMapper();

    @Test
    @DisplayName("KMDB Result를 MovieDto로 변환")
    void KMDB_Result를_MovieDto로_변환() {
        // given
        KmdbResponse.Result result = createTestResult();

        // when
        KmdbMovieDto dto = mapper.toDto(result);

        // then - 기본 정보
        assertThat(dto.getDocId()).isEqualTo("F63242");
        assertThat(dto.getMovieId()).isEqualTo("F");
        assertThat(dto.getMovieSeq()).isEqualTo("63242");
        assertThat(dto.getTitle()).isEqualTo("극장판 귀멸의 칼날");
        assertThat(dto.getGenre()).isEqualTo("액션");
        assertThat(dto.getRepRlsDate()).isEqualTo("20250822");

        // then - 포스터 추출
        assertThat(dto.getPoster()).isEqualTo("http://poster1.jpg");
        assertThat(dto.getPosters()).containsExactly("http://poster1.jpg", "http://poster2.jpg");

        // then - 줄거리 추출 (한국어 우선)
        assertThat(dto.getPlot()).contains("혈귀와의 결전");

        // then - 인물 추출 (출연/감독/원작만)
        assertThat(dto.getStaffs()).hasSize(2);
        assertThat(dto.getStaffs().get(0).getName()).isEqualTo("소토자키 하루오");
        assertThat(dto.getStaffs().get(0).getRoleGroup()).isEqualTo("감독");
        assertThat(dto.getStaffs().get(1).getName()).isEqualTo("하나에 나츠키");
        assertThat(dto.getStaffs().get(1).getRoleGroup()).isEqualTo("출연");
    }

    @Test
    @DisplayName("포스터 첫번째만 추출")
    void 포스터_첫번째만_추출() {
        // given
        KmdbResponse.Result result = new KmdbResponse.Result();
        result.setPosters("http://p1.jpg|http://p2.jpg|http://p3.jpg");

        // when
        KmdbMovieDto dto = mapper.toDto(result);

        // then
        assertThat(dto.getPoster()).isEqualTo("http://p1.jpg");
        assertThat(dto.getPosters()).hasSize(3);
    }

    @Test
    @DisplayName("줄거리 한국어 우선")
    void 줄거리_한국어_우선() {
        // given
        KmdbResponse.Plot koreanPlot = new KmdbResponse.Plot();
        koreanPlot.setPlotLang("한국어");
        koreanPlot.setPlotText("한국어 줄거리");

        KmdbResponse.Plot englishPlot = new KmdbResponse.Plot();
        englishPlot.setPlotLang("영어");
        englishPlot.setPlotText("English plot");

        KmdbResponse.Plots plots = new KmdbResponse.Plots();
        plots.setPlot(List.of(englishPlot, koreanPlot));

        KmdbResponse.Result result = new KmdbResponse.Result();
        result.setPlots(plots);

        // when
        KmdbMovieDto dto = mapper.toDto(result);

        // then
        assertThat(dto.getPlot()).isEqualTo("한국어 줄거리");
    }

    @Test
    @DisplayName("인물 필터링 - 출연/감독/원작만")
    void 인물_필터링_출연_감독_원작만() {
        // given
        KmdbResponse.Staff director = new KmdbResponse.Staff();
        director.setStaffNm("감독1");
        director.setStaffRoleGroup("감독");

        KmdbResponse.Staff actor = new KmdbResponse.Staff();
        actor.setStaffNm("배우1");
        actor.setStaffRoleGroup("출연");

        KmdbResponse.Staff producer = new KmdbResponse.Staff();
        producer.setStaffNm("제작자");
        producer.setStaffRoleGroup("제작사");  // 필터링됨

        KmdbResponse.Staffs staffs = new KmdbResponse.Staffs();
        staffs.setStaff(List.of(director, actor, producer));

        KmdbResponse.Result result = new KmdbResponse.Result();
        result.setStaffs(staffs);

        // when
        KmdbMovieDto dto = mapper.toDto(result);

        // then
        assertThat(dto.getStaffs()).hasSize(2);  // 제작자 제외
        assertThat(dto.getStaffs()).extracting("roleGroup")
                .containsExactly("감독", "출연");
    }

    @Test
    @DisplayName("null 안전성 테스트")
    void null_안전성_테스트() {
        // given
        KmdbResponse.Result result = new KmdbResponse.Result();
        result.setTitle("제목만 있음");
        // 나머지는 null

        // when
        KmdbMovieDto dto = mapper.toDto(result);

        // then - NPE 발생 안 함
        assertThat(dto.getTitle()).isEqualTo("제목만 있음");
        assertThat(dto.getPoster()).isNull();
        assertThat(dto.getPosters()).isEmpty();
        assertThat(dto.getStaffs()).isEmpty();
        assertThat(dto.getPlot()).isNull();
    }

    private KmdbResponse.Result createTestResult() {
        // 테스트용 Result 객체 생성
        KmdbResponse.Result result = new KmdbResponse.Result();
        result.setDOCID("F63242");
        result.setMovieId("F");
        result.setMovieSeq("63242");
        result.setTitle("극장판 귀멸의 칼날");
        result.setGenre("액션");
        result.setRepRlsDate("20250822");
        result.setPosters("http://poster1.jpg|http://poster2.jpg");

        // 줄거리
        KmdbResponse.Plot plot = new KmdbResponse.Plot();
        plot.setPlotLang("한국어");
        plot.setPlotText("다가오는 혈귀와의 결전");
        KmdbResponse.Plots plots = new KmdbResponse.Plots();
        plots.setPlot(List.of(plot));
        result.setPlots(plots);

        // 감독
        KmdbResponse.Staff director = new KmdbResponse.Staff();
        director.setStaffNm("소토자키 하루오");
        director.setStaffRoleGroup("감독");

        // 배우
        KmdbResponse.Staff actor = new KmdbResponse.Staff();
        actor.setStaffNm("하나에 나츠키");
        actor.setStaffRoleGroup("출연");

        KmdbResponse.Staffs staffs = new KmdbResponse.Staffs();
        staffs.setStaff(List.of(director, actor));
        result.setStaffs(staffs);

        return result;
    }
}
