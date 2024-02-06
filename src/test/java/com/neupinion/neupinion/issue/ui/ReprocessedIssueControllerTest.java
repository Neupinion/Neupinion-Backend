package com.neupinion.neupinion.issue.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("classpath:initialize_schema.sql")
class ReprocessedIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @DisplayName("GET /reprocessed-issue?date={date} 로 요청을 보내는 경우, 상태 코드 200과 해당 날짜의 재가공 이슈 리스트를 반환한다.")
    @Test
    void findReprocessedIssues() {
        // given
        final String date = "20240206";
        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T00:00:00Z"), ZoneId.systemDefault());
        final Clock otherClock = Clock.fixed(Instant.parse("2024-03-06T00:00:00Z"), ZoneId.systemDefault());

        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목1", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목2", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목3", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목4", "image", Category.SOCIETY, clock));
        reprocessedIssueRepository.save(ReprocessedIssue.forSave("재가공 이슈 제목5", "image", Category.ECONOMY, otherClock));

        // when
        final var response = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue?date={date}", date)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList(".", ReprocessedIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(response).hasSize(4),
            () -> assertThat(response).extracting("id").containsExactlyInAnyOrder(
                issue1.getId(), issue2.getId(), issue3.getId(), issue4.getId()
            )
        );
    }

    @DisplayName("POST /reprocessed-issue 로 요청을 보내는 경우, 상태 코드 201과 해당 날짜의 재가공 이슈를 생성한다.")
    @Test
    void saveReprocessedIssue() {
        // given
        final var request = ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", "ECONOMY");

        // when
        final var response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("Location");

        // then
        assertThat(reprocessedIssueRepository.existsById(valueOf(response.substring(response.lastIndexOf("/") + 1))))
            .isTrue();
    }
}
