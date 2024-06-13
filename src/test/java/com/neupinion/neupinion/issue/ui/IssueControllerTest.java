package com.neupinion.neupinion.issue.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.auth.application.TokenProvider;
import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.TimelineResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTrustVote;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.RelatableStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class IssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private FollowUpIssueTrustVoteRepository followUpIssueTrustVoteRepository;

    @Autowired
    private IssueStandRepository issueStandRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("GET /issue/{issueId}/integrated-result 요청을 받아 상태 코드 200과 통합 투표 결과를 반환한다.")
    @Test
    void getIntegratedVoteResult() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "이미지링크", "캡션", Category.ECONOMY));

        final List<IssueStand>  issueStands = List.of(
            issueStandRepository.save(IssueStand.forSave("찬성", reprocessedIssue.getId())),
            issueStandRepository.save(IssueStand.forSave("반대", reprocessedIssue.getId()))
        );

        final RelatableStand relatableStand1 = new RelatableStand(issueStands.get(0).getId(), true, issueStands.get(1).getId(), false);
        final RelatableStand relatableStand2 = new RelatableStand(issueStands.get(0).getId(), false, issueStands.get(1).getId(), true);

        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 1L, relatableStand1));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 2L, relatableStand2));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈1 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue1.getId(), 1L, relatableStand1));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue1.getId(), 2L, relatableStand1));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈2 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue2.getId(), 1L, relatableStand2));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue2.getId(), 2L, relatableStand1));

        // when
        final var response = RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(1L))
            .contentType(ContentType.JSON)
            .when().log().all()
            .get("/issue/{issueId}/integrated-result", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .as(IntegratedVoteResultResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getMostVotedStand()).isEqualTo("찬성"),
            () -> assertThat(response.getMostVotedCount()).isEqualTo(4),
            () -> assertThat(response.getTotalVoteCount()).isEqualTo(6),
            () -> assertThat(response.getVoteResults()).hasSize(3),
            () -> assertThat(response.getVoteRankings()).usingRecursiveComparison()
                .comparingOnlyFields("stand")
                .isEqualTo(List.of("찬성", "반대")),
            () -> assertThat(response.getVoteRankings()).usingRecursiveComparison()
                .comparingOnlyFields("relatablePercentage")
                .isEqualTo(List.of(66, 33))
        );
    }

    @DisplayName("GET /issue/{issueId}/time-line 요청을 받아 상태 코드 200과 해당 이슈의 타임라인을 반환한다.")
    @Test
    void getIssueTimeLine() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "이미지링크", "캡션", Category.ECONOMY));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈1 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈2 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));

        // when
        final var responses = RestAssured.given()
            .when().log().all()
            .get("/issue/{issueId}/time-line", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .jsonPath().getList(".", TimelineResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(List.of(reprocessedIssue.getId(), followUpIssue1.getId(), followUpIssue2.getId()))
        );
    }
}
