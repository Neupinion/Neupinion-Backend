package com.neupinion.neupinion.opinion.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.auth.application.TokenProvider;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.opinion.application.dto.IssueOpinionResponse;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class IntegratedOpinionControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private FollowUpIssueParagraphRepository followUpIssueParagraphRepository;

    @Autowired
    private FollowUpIssueOpinionRepository followUpIssueOpinionRepository;

    @Autowired
    private FollowUpIssueOpinionLikeRepository followUpIssueOpinionLikeRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("GET /issue/{issueId}/opinion 요청을 받아 상태 코드 200 과 해당 이슈의 전체 의견을 최신순으로 조회한다.")
    @Test
    void getOpinions() {
        // given
        final Long reprocessedIssueId = 1L;
        final Long memberId = memberRepository.save(Member.forSave("뉴피1", "image1")).getId();

        final ReprocessedIssueParagraph reprocessedIssueParagraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("문단 내용1", true, reprocessedIssueId));
        final ReprocessedIssueOpinion reprocessedIssueOpinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, true, memberId,
                                            "의견 내용1"));
        final ReprocessedIssueOpinion reprocessedIssueOpinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, true, memberId,
                                            "의견 내용2"));

        final FollowUpIssueParagraph followUpIssueParagraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("문단 내용1", true, followUpIssueRepository.save(
                FollowUpIssue.forSave("후속 이슈 제목1", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                      reprocessedIssueId)).getId()));

        final FollowUpIssueOpinion followUpIssueOpinion1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, true, memberId,
                                         "의견 내용1"));
        final FollowUpIssueOpinion followUpIssueOpinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, false, memberId,
                                         "의견 내용2"));
        final FollowUpIssueOpinion followUpIssueOpinion3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, true, memberId,
                                         "의견 내용3"));

        // when
        final var responses = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .when()
            .get("/issue/{issueId}/opinion", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .jsonPath().getList(".", IssueOpinionResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(5),
            () -> assertThat(responses).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(List.of(followUpIssueOpinion3.getId(), followUpIssueOpinion2.getId(),
                                   followUpIssueOpinion1.getId(), reprocessedIssueOpinion2.getId(),
                                   reprocessedIssueOpinion1.getId()))
        );
    }

    @DisplayName("GET /issue/{issueId}/opinion?viewMode=doubt&orderMode=popular 요청을 받아 상태 코드 200 과 해당 이슈의 전체 의심 의견을 좋아요순으로 조회한다.")
    @Test
    void getOpinionsByLikeCount() {
        // given
        final Long reprocessedIssueId = 1L;
        final Long memberId = memberRepository.save(Member.forSave("뉴피1", "image1")).getId();
        final Long member2Id = memberRepository.save(Member.forSave("뉴피2", "image1")).getId();

        final ReprocessedIssueParagraph reprocessedIssueParagraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("문단 내용1", true, reprocessedIssueId));
        final ReprocessedIssueOpinion reprocessedIssueOpinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, false, memberId,
                                            "의견 내용1"));
        final ReprocessedIssueOpinion reprocessedIssueOpinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, true, memberId,
                                            "의견 내용2"));

        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(member2Id, reprocessedIssueOpinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion2.getId()));

        final FollowUpIssueParagraph followUpIssueParagraph = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("문단 내용1", true, followUpIssueRepository.save(
                FollowUpIssue.forSave("후속 이슈 제목1", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                      reprocessedIssueId)).getId()));

        final FollowUpIssueOpinion followUpIssueOpinion1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, true, memberId,
                                         "의견 내용1"));
        final FollowUpIssueOpinion followUpIssueOpinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, false, memberId,
                                         "의견 내용2"));
        final FollowUpIssueOpinion followUpIssueOpinion3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph.getId(), reprocessedIssueId, true, memberId,
                                         "의견 내용3"));

        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion1.getId()));
        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(member2Id, followUpIssueOpinion1.getId()));
        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion2.getId()));
        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion3.getId()));

        // when
        final var responses = RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .when().log().all()
            .get("/issue/{issueId}/opinion?orderMode=popular&viewMode=doubt", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .jsonPath().getList(".", IssueOpinionResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(2),
            () -> assertThat(responses).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(List.of(followUpIssueOpinion2.getId(), reprocessedIssueOpinion1.getId()))
        );
    }

    @DisplayName("GET /issue/{issueId}/opinion/top 요청을 받아 상태 코드 200 과 해당 이슈의 의견 5개를 좋아요 개수 내림차순으로 반환한다.")
    @Test
    void getTopOpinions() {
        // given
        final Long reprocessedIssueId = 1L;
        final Long memberId = memberRepository.save(Member.forSave("뉴피1", "image1")).getId();

        final ReprocessedIssueParagraph reprocessedIssueParagraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("문단 내용1", true, reprocessedIssueId));
        final ReprocessedIssueOpinion reprocessedIssueOpinion1 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, true, memberId,
                                            "의견 내용1"));
        final ReprocessedIssueOpinion reprocessedIssueOpinion2 = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(reprocessedIssueParagraph1.getId(), reprocessedIssueId, true, memberId,
                                            "의견 내용2"));

        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion1.getId()));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion1.getId()));

        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion2.getId()));
        reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, reprocessedIssueOpinion2.getId()));

        final Long followUpIssueId = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssueId)).getId();

        final FollowUpIssueParagraph followUpIssueParagraph1 = followUpIssueParagraphRepository.save(
            FollowUpIssueParagraph.forSave("문단 내용1", true, followUpIssueId));
        final FollowUpIssueOpinion followUpIssueOpinion1 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph1.getId(), followUpIssueId, true, memberId, "의견 내용1"));
        final FollowUpIssueOpinion followUpIssueOpinion2 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph1.getId(), followUpIssueId, false, memberId, "의견 내용2"));
        final FollowUpIssueOpinion followUpIssueOpinion3 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph1.getId(), followUpIssueId, true, memberId, "의견 내용3"));
        final FollowUpIssueOpinion followUpIssueOpinion4 = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(followUpIssueParagraph1.getId(), followUpIssueId, false, memberId, "의견 내용4"));

        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion1.getId()));
        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion2.getId()));
        followUpIssueOpinionLikeRepository.save(
            FollowUpIssueOpinionLike.forSave(memberId, followUpIssueOpinion3.getId()));

        // when
        final var responses = RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .when()
            .get("/issue/{issueId}/opinion/top", reprocessedIssueId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .jsonPath().getList(".", IssueOpinionResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(5),
            () -> assertThat(responses).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(List.of(reprocessedIssueOpinion1.getId(), reprocessedIssueOpinion2.getId(),
                                   followUpIssueOpinion1.getId(), followUpIssueOpinion2.getId(),
                                   followUpIssueOpinion3.getId()))
        );
    }
}
