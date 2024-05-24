package com.neupinion.neupinion.opinion.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import com.neupinion.neupinion.auth.application.OAuthType;
import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.MyOpinionResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionParagraphResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionUpdateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

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
        followUpIssueParagraphRepository.save(
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

    @DisplayName("DELETE /follow-up-issue/opinion/{opinionId} 요청을 보내는 경우, 상태 코드 204를 반환한다.")
    @Test
    void deleteFollowUpIssueOpinion() {
        // given
        final long followUpIssueId = 1L;
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("내용", false, followUpIssueId));
        final long memberId = 1L;
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(paragraph.getId(), followUpIssueId, true, memberId, "내용"));

        // when
        // then
        RestAssured.given().log().all()
            .when().log().all()
            .delete("/follow-up-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("DELETE /reprocessed-issue/opinion/{opinionId} 요청을 보내는 경우, 상태 코드 204를 반환한다.")
    @Test
    void deleteReprocessedIssueOpinion() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", false, reprocessedIssueId));
        final long memberId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용"));

        // when
        // then
        RestAssured.given().log().all()
            .when().log().all()
            .delete("/reprocessed-issue/opinion/{opinionId}", opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/opinion 요청을 보내는 경우, 상태 코드 200과 재가공 이슈의 전체 의견을 반환한다.")
    @Test
    void getReprocessedIssueOpinions() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        when(memberRepository.getMemberById(1L))
            .thenReturn(new Member(1L, "뉴피1", "https://neupinion/image/1", OAuthType.GOOGLE, "googleId"));
        when(memberRepository.getMemberById(2L))
            .thenReturn(new Member(2L, "뉴피2", "https://neupinion/image/2", OAuthType.GOOGLE, "googleId2"));
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, 2L, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, true, 1L, "내용2"));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(1L, opinion.getId()));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/opinion", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", ReprocessedIssueOpinionResponse.class);

        // 현재 멤버는 1L 로 고정된 상태
        // then
        assertAll(
            () -> assertThat(responses).hasSize(2),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getId)
                .containsExactly(opinion2.getId(), opinion.getId()),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getLikeCount)
                .containsExactly(0, 1),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getIsLiked)
                .containsExactly(false, true)
        );
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/opinion?viewMode=doubt 요청을 보내는 경우, 상태 코드 200과 재가공 이슈의 의심 의견을 반환한다.")
    @Test
    void getReprocessedIssueOpinionsByDoubt() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        when(memberRepository.getMemberById(1L))
            .thenReturn(new Member(1L, "뉴피1", "https://neupinion/image/1", OAuthType.GOOGLE, "googleId"));
        when(memberRepository.getMemberById(2L))
            .thenReturn(new Member(2L, "뉴피2", "https://neupinion/image/2", OAuthType.GOOGLE, "googleId"));
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, 2L, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, false, 1L, "내용2"));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(1L, opinion.getId()));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/opinion?viewMode=doubt", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", ReprocessedIssueOpinionResponse.class);

        // 현재 멤버는 1L 로 고정된 상태
        // then
        assertAll(
            () -> assertThat(responses).hasSize(1),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getId)
                .containsExactly(opinion2.getId()),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getLikeCount)
                .containsExactly(0),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getIsLiked)
                .containsExactly(false)
        );
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/opinion/top 요청을 보내는 경우, 상태 코드 200과 좋아요가 가장 많은 5개의 의견을 반환한다.")
    @Test
    void getTopReprocessedIssueOpinions() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));

        final long memberId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, true, memberId, "내용2"));
        final ReprocessedIssueOpinion opinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용3"));
        final ReprocessedIssueOpinion opinion4 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용4"));
        final ReprocessedIssueOpinion opinion5 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용5"));
        final ReprocessedIssueOpinion opinion6 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용6"));

        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion3.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion4.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion5.getId()));
        reprocessedIssueOpinionLikeRepository.save(ReprocessedIssueOpinionLike.forSave(memberId, opinion6.getId()));

        when(memberRepository.getMemberById(memberId))
            .thenReturn(Member.forSave("뉴피1", "https://neupinion/image/1"));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/opinion/top", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", ReprocessedIssueOpinionResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(5),
            () -> assertThat(responses).extracting(ReprocessedIssueOpinionResponse::getId)
                .containsExactly(opinion2.getId(), opinion.getId(), opinion4.getId(), opinion3.getId(),
                                 opinion6.getId())
        );
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/opinion/paragraph 요청을 보내는 경우, 상태 코드 200과 전체 문단에 대한 의견을 반환한다.")
    @Test
    void getReprocessedIssueOpinionsOrderByParagraph() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));

        final long memberId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, true, memberId, "내용2"));
        final ReprocessedIssueOpinion opinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용3"));

        when(memberRepository.getMemberById(memberId))
            .thenReturn(Member.forSave("뉴피1", "https://neupinion/image/1"));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/opinion/paragraph", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", OpinionParagraphResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).extracting(OpinionParagraphResponse::getId)
                .containsExactly(paragraph.getId(), paragraph2.getId(), paragraph3.getId()),
            () -> assertThat(responses.get(0).getOpinions().get(0).getId()).isEqualTo(opinion.getId()),
            () -> assertThat(responses.get(1).getOpinions().get(0).getId()).isEqualTo(opinion2.getId()),
            () -> assertThat(responses.get(2).getOpinions().get(0).getId()).isEqualTo(opinion3.getId())
        );
    }

    @DisplayName("GET /reprocessed-issue/{issueId}/opinion/paragraph?viewMode=doubt 요청을 보내는 경우, 상태 코드 200과 전체 문단에 대한 의견을 반환한다.")
    @Test
    void getReprocessedIssueOpinionsOrderByParagraphByDoubt() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));

        final long memberId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph.getId(), reprocessedIssueId, true, memberId, "내용1"));
        final ReprocessedIssueOpinion opinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph2.getId(), reprocessedIssueId, false, memberId, "내용2"));
        final ReprocessedIssueOpinion opinion3 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(paragraph3.getId(), reprocessedIssueId, true, memberId, "내용3"));

        when(memberRepository.getMemberById(memberId))
            .thenReturn(Member.forSave("뉴피1", "https://neupinion/image/1"));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{issueId}/opinion/paragraph?viewMode=doubt", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", OpinionParagraphResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(1),
            () -> assertThat(responses).extracting(OpinionParagraphResponse::getId).containsExactly(paragraph2.getId()),
            () -> assertThat(responses.get(0).getOpinions().get(0).getId()).isEqualTo(opinion2.getId())
        );
    }
}
