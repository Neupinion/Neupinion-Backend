package com.neupinion.neupinion.issue.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Issue;
import com.neupinion.neupinion.issue.domain.repository.IssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class ReprocessedIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @DisplayName("GET /reprocessed-issue?date={date} 로 요청을 보내는 경우, 상태 코드 200과 해당 날짜의 재가공 이슈 리스트를 반환한다.")
    @Test
    void findReprocessedIssues() {
        // given
        final String date = "20240204";

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
            () -> assertThat(response).hasSize(4)
        );
    }

    @DisplayName("POST /reprocessed-issue 로 요청을 보내는 경우, 상태 코드 201과 해당 날짜의 재가공 이슈를 생성한다.")
    @Test
    void saveReprocessedIssue() {
        // given
        final Issue savedIssue = issueRepository.save(new Issue("이슈 제목"));
        final var request = ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", "ECONOMY", savedIssue.getId());

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

    @DisplayName("POST /reprocessed-issue 로 요청을 보낼 때 존재하지 않는 이슈를 참조하는 경우 상태 코드 400과 예외를 반환한다.")
    @Test
    void saveReprocessedIssue_notExistIssueException() {
        // given
        final Long notExistedId = 0L;
        final var request = ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", "ECONOMY", notExistedId);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
