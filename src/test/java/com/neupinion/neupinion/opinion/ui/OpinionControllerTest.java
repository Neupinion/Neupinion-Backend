package com.neupinion.neupinion.opinion.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.MyOpinionResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionUpdateRequest;
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
                                                                                               followUpIssueId, "내용",
                                                                                               true);

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
                                                                                               followUpIssueId, "내용",
                                                                                               true);
        final long memberId = 1L;
        followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, memberId, "내용"));

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
                                                                                               "내용", true);

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
                                                                                                     "내용", true);

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
                                                                                                     "내용", true);
        final long memberId = 1L;
        reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용"));

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
                                                                                                     "내용", true);

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

    @DisplayName("PATCH /reprocessed-issue/opinion/{opinionId} 요청을 보내는 경우, 상태 코드 204를 반환한다.")
    @Test
    void updateReprocessedIssueOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, 1L, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/reprocessed-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("PATCH /reprocessed-issue/opinion/{opinionId} 요청을 보낼 때, 해당 의견이 존재하지 않는 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateReprocessedIssueOpinion_NotFoundOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph.getId(), "수정된 내용", true);

        // when
        // then
        final long notExistedOpinionId = Long.MAX_VALUE;
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/reprocessed-issue/opinion/{opinionId}", notExistedOpinionId)
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("PATCH /reprocessed-issue/opinion/{opinionId} 요청을 보낼 때, 해당 의견이 다른 회원의 것인 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateReprocessedIssueOpinion_NotMatchedOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final long memberId = 1L;
        final long otherMemberId = Long.MAX_VALUE;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, otherMemberId, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/reprocessed-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("PATCH /reprocessed-issue/opinion/{opinionId} 요청을 보낼 때, 해당 문단이 다른 재가공 이슈에 속하는 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateReprocessedIssueOpinion_OpinionForOtherReprocessedIssue() {
        // given
        final long reprocessedIssueId = 1L;
        final long reprocessedIssueId2 = 2L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId2));
        final long memberId = 1L;
        final long otherMemberId = Long.MAX_VALUE;

        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, otherMemberId, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/reprocessed-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("PATCH /follow-up-issue/opinion/{opinionId} 요청을 보내는 경우, 상태 코드 204를 반환한다.")
    @Test
    void updateFollowUpIssueOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueParagraph paragraph2 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final long memberId = 1L;
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, memberId, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/follow-up-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("PATCH /follow-up-issue/opinion/{opinionId} 요청을 보낼 때, 해당 의견이 존재하지 않는 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateFollowUpIssueOpinion_NotFoundOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueParagraph paragraph2 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        final long notExistedOpinionId = Long.MAX_VALUE;
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/follow-up-issue/opinion/{opinionId}", notExistedOpinionId)
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("PATCH /follow-up-issue/opinion/{opinionId} 요청을 보낼 때, 해당 의견이 다른 회원의 것인 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateFollowUpIssueOpinion_NotMatchedOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueParagraph paragraph2 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final long memberId = 1L;
        final long otherMemberId = Long.MAX_VALUE;
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, otherMemberId, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/follow-up-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("PATCH /follow-up-issue/opinion/{opinionId} 요청을 보낼 때, 해당 문단이 다른 후속 이슈에 속하는 경우, 상태 코드 400를 반환한다.")
    @Test
    void updateFollowUpIssueOpinion_OpinionForOtherReprocessedIssue() {
        // given
        final long followUpIssueId = 1L;
        final long otherFollowUpIssueId = 2L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueParagraph paragraph2 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, otherFollowUpIssueId));
        final long memberId = 1L;
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, memberId, "내용"));

        final OpinionUpdateRequest request = OpinionUpdateRequest.of(paragraph2.getId(), "수정된 내용", true);

        // when
        // then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .patch("/follow-up-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("GET /follow-up-issue/{issueId}/me 요청을 보내는 경우, 상태 코드 200을 반환한다.")
    @Test
    void getMyFollowUpIssueOpinions() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final FollowUpIssueParagraph paragraph2 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final long memberId = 1L;
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, memberId, "내용"));
        final FollowUpIssueOpinion opinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph2.getId(), followUpIssueId, true, memberId, "내용"));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/follow-up-issue/{issueId}/me", followUpIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", MyOpinionResponse.class);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(MyOpinionResponse::getId)
            .containsExactlyInAnyOrder(opinion.getId(), opinion2.getId());
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/me 요청을 보내는 경우, 상태 코드 200을 반환한다.")
    @Test
    void getMyReprocessedIssueOpinions() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final long memberId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, true, memberId, "내용"));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/me", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", MyOpinionResponse.class);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(MyOpinionResponse::getId)
            .containsExactlyInAnyOrder(opinion.getId(), opinion2.getId());
    }
}
