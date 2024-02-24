package com.neupinion.neupinion.opinion.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class OpinionControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private FollowUpIssueParagraphRepository followUpIssueParagraphRepository;

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @DisplayName("POST /follow-up-issue/opinion 요청을 보내는 경우, 상태 코드 201을 반환한다.")
    @Test
    void createFollowUpIssueOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                               followUpIssueId, "내용");

        // when
        final var response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/follow-up-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("Location");

        // then
        assertThat(
            followUpIssueOpinionRepository.existsById(valueOf(response.substring(response.lastIndexOf("/") + 1))))
            .isTrue();
    }

    @DisplayName("POST /reprocessed-issue/opinion 요청을 보낼 때 이미 동일한 단락에 대한 의견이 존재하는 경우, 상태 코드 400을 반환한다.")
    @Test
    void createFollowUpIssueOpinion_alreadyExistedOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                               followUpIssueId, "내용");
        final long memberId = 1L;
        followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, memberId, "내용"));

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/follow-up-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("POST /follow-up-issue/opinion 요청을 보낼 때, 등록하고자 하는 단락이 해당 후속 이슈에 속하지 않는 경우, 상태 코드 400을 반환한다.")
    @Test
    void createFollowUpIssue_paragraphForOtherFollowUpIssue() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final long otherFollowUpIssueId = 2L;
        final FollowUpIssueOpinionCreateRequest request = FollowUpIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                               otherFollowUpIssueId,
                                                                                               "내용");

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/follow-up-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("POST /reprocessed-issue/opinion 요청을 보내는 경우, 상태 코드 201을 반환한다.")
    @Test
    void createReprocessedIssueOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                                     reprocessedIssueId,
                                                                                                     "내용");

        // when
        final var response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("Location");

        // then
        assertThat(
            reprocessedIssueOpinionRepository.existsById(valueOf(response.substring(response.lastIndexOf("/") + 1))))
            .isTrue();
    }

    @DisplayName("POST /reprocessed-issue/opinion 요청을 보낼 때 이미 동일한 단락에 대한 의견이 존재하는 경우, 상태 코드 400을 반환한다.")
    @Test
    void createReprocessedIssueOpinion_alreadyExistedOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                                     reprocessedIssueId,
                                                                                                     "내용");
        final long memberId = 1L;
        reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, memberId, "내용"));

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("POST /reprocessed-issue/opinion 요청을 보낼 때, 등록하고자 하는 단락이 해당 재가공 이슈에 속하지 않는 경우, 상태 코드 400을 반환한다.")
    @Test
    void createReprocessedIssue_paragraphForOtherReprocessedIssue() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final long otherReprocessedIssueId = 2L;
        final ReprocessedIssueOpinionCreateRequest request = ReprocessedIssueOpinionCreateRequest.of(paragraph.getId(),
                                                                                                     otherReprocessedIssueId,
                                                                                                     "내용");

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue/opinion")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
