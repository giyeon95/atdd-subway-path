package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.val;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.acceptance.test.utils.Lines;
import nextstep.subway.acceptance.test.utils.Stations;
import nextstep.subway.utils.ApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     * @see nextstep.subway.ui.LineController#createLine
     */
    @DisplayName("지하철 노선 생성 테스트")
    @Test
    void 지하철_노선_생성_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 같은 이름으로 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createLine
     */
    @DisplayName("[예외]지하철 노선 이름 중복 생성 방지 테스트")
    @Test
    void 지하철_노선_이름_중복_생성_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * When 지하철 노선 생성을 요청 했을 때 상행 역 정보가 없으면,
     * Then 지하철 노선 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createLine
     */
    @DisplayName("[예외]지하철 노선 상행 등록정보 없음 방지 테스트")
    @Test
    void 지하철_노선_상행_등록정보_없음_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_생성(Lines.GTXA노선_상행_정보없음);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * When 지하철 노선 생성을 요청 했을 때 하행 역 정보가 없으면,
     * Then 지하철 노선 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createLine
     */
    @DisplayName("[예외]지하철 노선 하행 등록정보 없음 방지 테스트")
    @Test
    void 지하철_노선_하행_등록정보_없음_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_생성(Lines.GTXA노선_하행_정보없음);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * When 지하철 노선 생성을 요청 했을 때 노선 거리 정보가 없거나 음수이면,
     * Then 지하철 노선 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createLine
     */
    @DisplayName("[예외]지하철 노선 거리 정보 없음 방지 테스트")
    @Test
    void 지하철_노선_거리_정보_없음_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_생성(Lines.GTXA노선_거리_음수);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     * @see nextstep.subway.ui.LineController#findAllLines()
     */
    @DisplayName("지하철 노선 목록 조회 테스트")
    @Test
    void 지하철_노선_목록_조회_테스트() {
        // given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        ApiUtil.지하철역_생성(Stations.강남역);
        ApiUtil.지하철역_생성(Stations.양재역);
        ApiUtil.지하철_노선_생성(Lines.신분당선);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_전체_리스트_조회();

        // then
        List<String> lineNames = response.body().jsonPath().getList("name");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineNames).contains("GTX-A", "신분당선");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     * @see nextstep.subway.ui.LineController#getLine
     */
    @DisplayName("지하철 노선 조회 테스트")
    @Test
    void 지하철_노선_조회_테스트() {
        // given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_단건_조회(1L);

        // then
        String lineName = response.body().jsonPath().get("name").toString();
        String lineColor = response.body().jsonPath().get("color").toString();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineName).isEqualTo("GTX-A");
        assertThat(lineColor).isEqualTo("bg-red-900");
    }

    /**
     * Given 지하철 노선 생성 및 구간등록 요청 하고,
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선 역을 응답받는다
     * @see nextstep.subway.ui.LineController#getLine
     */
    @DisplayName("지하철 노선역 조회 테스트")
    @Test
    void 지하철_노선역_조회_테스트() {
        // given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);
        ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_서울역_삼성역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_단건_조회(1L);

        // then
        List<HashMap<String, String>> stations = response.body().jsonPath().getList("stations");
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).get("name").equals("연신내")).isTrue();
        assertThat(stations.get(1).get("name").equals("서울역")).isTrue();
        assertThat(stations.get(2).get("name").equals("삼성역")).isTrue();
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     * @see nextstep.subway.ui.LineController#updateLine
     */
    @DisplayName("지하철 노선 수정 테스트")
    @Test
    void 지하철_노선_수정_테스트() {
        // given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_수정(1L, Lines.노선색상);

        // then
        ExtractableResponse<Response> updatedLine = ApiUtil.지하철_노선_단건_조회(1L);
        String lineName = updatedLine.body().jsonPath().get("name").toString();
        String lineColor = updatedLine.body().jsonPath().get("color").toString();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineName).isEqualTo("GTX-A");
        assertThat(lineColor).isEqualTo("bg-red-800");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     * @see nextstep.subway.ui.LineController#deleteLine
     */
    @DisplayName("지하철 노선 삭제 테스트")
    @Test
    void 지하철_노선_삭제_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        ExtractableResponse<Response> deleteResponse = ApiUtil.지하철_노선_삭제(1L);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * When 지하철 노선 구간 생성을 요청 하면,
     * Then 지하철 노선 구간 생성이 성공한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("지하철 노선 구간 등록 테스트")
    @Test
    void 지하철_노선_구간_하행_등록_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        ExtractableResponse<Response> response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_서울역_삼성역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 연신내역, 서울역, 삼성역을 생성하고,
     * Given 연신내, 삼성역을 연결하는 10거리의 GTX-A 노선 생성 후,
     * When 연신내, 서울역을 구간으로 하는 GTX-A 노선의 새로운 구간 추가하면,
     * Then 지하철 노선 구간 생성이 성공한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("지하철 노선 구간 중간 등록 테스트")
    @Test
    void 지하철_노선_구간_중간_등록_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_삼성역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 연신내역, 서울역, 삼성역을 생성하고,
     * Given 연신내, 삼성역을 연결하는 5거리의 GTX-A 노선 생성 후,
     * When 연신내, 서울역을 구간으로 하는 5거리의 GTX-A 노선의 새로운 구간을 추가하면,
     * Then 지하철 노선 구간 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("[예외]지하철 노선 구간 중간 등록 기존 구간의 길이보다 길거나 같음 방지 테스트")
    @Test
    void 지하철_노선_구간_중간_등록_기존_구간의_길이보다_길거나_같음_방지_테스트() {
        //given
        Lines.GTXA노선_연신내_삼성역.setDistance(5);
        Lines.GTXA노선_구간_연신내_서울역.setDistance(5);

        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_삼성역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * Given 연신내역, 서울역을 생성하고,
     * Given 연신내, 서울역을 연결하는 10거리의 GTX-A 노선 생성 후,
     * When 연신내, 서울역을 구간으로 하는 5거리의 GTX-A 새로운 구간을 추가하면,
     * Then 지하철 노선 구간 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("[예외]지하철 노선 구간 중간 등록 기존 구간 역 정보와 같음 방지 테스트")
    @Test
    void 지하철_노선_구간_중간_등록_기존_구간의_역_정보와_같음_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * Given 연신내역, 서울역, 삼성역, 강남역을 생성하고,
     * Given 연신내, 삼성역을 연결하는 10거리의 GTX-A 노선 생성 후,
     * When 연신내, 서울역을 구간으로 하는 10거리의 GTX-A 새로운 구간을 추가하면,
     * Then 지하철 노선 구간 생성이 실패한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("[예외]지하철 기존 구간 역 정보와 하나도 맞지 많음 방지 테스트")
    @Test
    void 지하철_기존_구간_역_정보와_하나도_맞지_많음_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철역_생성(Stations.강남역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_연신내_서울역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * When 지하철 노선 구간 생성을 요청 시, 상행역은 해당 노선에 등록되어있는 하행역 아니면,
     * Then 지하철 노선 구건 생성에 실패한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("[예외]지하철 노선 구간 생성 시 기존 역의 하행역이 아닐경우 방지 테스트")
    @Test
    void 지하철_노선_구간_생성_시_기존_역의_하행역이_아닐경우_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_연신내역_삼성역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * When 지하철 노선 구간 생성을 요청 시, 하행역이 이미 존재할 경우,
     * Then 지하철 노선 구건 생성에 실패한다.
     * @see nextstep.subway.ui.LineController#createSection
     */
    @DisplayName("[예외]지하철 노선 구간 생성 시 하행역이 이미 존재함 방지 테스트")
    @Test
    void 지하철_노선_구간_생성_시_하행역이_이미_존재_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        val response = ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_서울역_연신내역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * Given 지하철 노선 생성 및 노선 구간 생성을 하고,
     * When 생성한 지하철 노선 구간 삭제를 요청 하면
     * Then 생성한 지하철 노선 구간 삭제가 성공한다.
     * @see nextstep.subway.ui.LineController#deleteSection
     */
    @DisplayName("지하철 노선 구간 삭제 테스트")
    @Test
    void 지하철_노선_구간_삭제_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철역_생성(Stations.삼성역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);
        ApiUtil.지하철_노선_구간_등록(1L, Lines.GTXA노선_구간_서울역_삼성역);

        // when
        val deleteResponse = ApiUtil.지하철_노선_구간_삭제(1L, Lines.GTXA노선_구간_삭제_서울역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선 생성 및 노선 구간 생성을 하고,
     * When 하행 종점이 아닌 지하철 노선 구간 삭제를 요청 하면
     * Then 생성한 지하철 노선 구간 삭제가 실패한다.
     * @see nextstep.subway.ui.LineController#deleteSection
     */
    @DisplayName("지하철 노선 한 구간만 존재할 경우 삭제 방지 테스트")
    @Test
    void 지하철_노선_한_구간만_존재할_경우_삭제_방지_테스트() {
        //given
        ApiUtil.지하철역_생성(Stations.연신내역);
        ApiUtil.지하철역_생성(Stations.서울역);
        ApiUtil.지하철_노선_생성(Lines.GTXA노선_연신내_서울역);

        // when
        val deleteResponse = ApiUtil.지하철_노선_구간_삭제(1L, Lines.GTXA노선_구간_삭제_서울역);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @BeforeEach
    public void 초기화() {
        Lines.파람_초기화();
        Stations.파람_초기화();
    }
}
