package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.ReprocessedIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class FollowUpIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueService reprocessedIssueService;

    @DisplayName("POST /follow-up-issue 요청을 보내는 경우, 상태 코드 201과 후속 이슈를 생성한다.")
    @Test
    void createFollowUpIssue() {
        // given
        final Long reprocessedIssueId = reprocessedIssueService.save(
            ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", Category.WORLD.name()));
        final FollowUpIssueCreateRequest request = FollowUpIssueCreateRequest.of("후속 이슈 제목", Category.WORLD.name(),
                                                                                 "https://neupinion.com/image.jpg",
                                                                                 FollowUpIssueTag.OFFICIAL_POSITION.name(),
                                                                                 reprocessedIssueId);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/follow-up-issue")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("Location");
    }

    @DisplayName("POST /follow-up-issue 요청의 재가공 이슈 id 가 유효하지 않은 경우, 상태 코드 400을 반환한다.")
    @Test
    void createFollowUpIssueWithInvalidReprocessedIssueId() {
        // given
        final long notExistedReprocessedId = 0L;
        final FollowUpIssueCreateRequest request = FollowUpIssueCreateRequest.of("후속 이슈 제목", Category.WORLD.name(),
                                                                                 "https://neupinion.com/image.jpg",
                                                                                 FollowUpIssueTag.OFFICIAL_POSITION.name(),
                                                                                 notExistedReprocessedId);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/follow-up-issue")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
