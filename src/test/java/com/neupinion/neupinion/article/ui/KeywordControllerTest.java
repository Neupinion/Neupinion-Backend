package com.neupinion.neupinion.article.ui;

import com.neupinion.neupinion.article.application.dto.KeywordCreateRequest;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class KeywordControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private IssueStandRepository issueStandRepository;

    @DisplayName("POST /issue/{issueId}/keyword 요청을 보내면, 이슈 키워드를 등록한다.")
    @Test
    void registerKeywords() {
        // given
        final long issueId = 1L;
        final IssueStand stand1 = issueStandRepository.save(IssueStand.forSave("stand1", issueId));
        final IssueStand stand2 = issueStandRepository.save(IssueStand.forSave("stand2", issueId));
        final KeywordCreateRequest request = new KeywordCreateRequest(
            stand1.getStand(),
            List.of("keyword1", "keyword2"),
            stand2.getStand(),
            List.of("keyword3", "keyword4")
        );

        // when
        // then
        RestAssured.given()
            .body(request)
            .contentType(ContentType.JSON)
            .when()
            .post("/issue/{issueId}/keyword", issueId)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }
}
