package com.neupinion.neupinion.issue.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.TimelineResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTrustVote;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.VoteStatus;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DisplayName("GET /issue/{issueId}/integrated-result 요청을 받아 상태 코드 200과 통합 투표 결과를 반환한다.")
    @Test
    void getIntegratedVoteResult() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "이미지링크", "캡션", "원본링크", Category.ECONOMY));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 1L, VoteStatus.HIGHLY_TRUSTED));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 2L, VoteStatus.SOMEWHAT_DISTRUSTED));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈1 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue1.getId(), 1L, VoteStatus.HIGHLY_TRUSTED));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue1.getId(), 2L, VoteStatus.HIGHLY_TRUSTED));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈2 제목", "이미지링크", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue2.getId(), 1L, VoteStatus.SOMEWHAT_DISTRUSTED));
        followUpIssueTrustVoteRepository.save(
            FollowUpIssueTrustVote.forSave(followUpIssue2.getId(), 2L, VoteStatus.SOMEWHAT_TRUSTED));

        // when
        final var response = RestAssured.given()
            .when().log().all()
            .get("/issue/{issueId}/integrated-result", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract()
            .as(IntegratedVoteResultResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getMostVoted()).isEqualTo(VoteStatus.HIGHLY_TRUSTED.getValue()),
            () -> assertThat(response.getMostVotedCount()).isEqualTo(3),
            () -> assertThat(response.getTotalVoteCount()).isEqualTo(6),
            () -> assertThat(response.getVoteResults()).hasSize(3),
            () -> assertThat(response.getVoteRankings()).usingRecursiveComparison()
                .comparingOnlyFields("voteStatus")
                .isEqualTo(List.of(VoteStatus.HIGHLY_TRUSTED.getValue(), VoteStatus.SOMEWHAT_DISTRUSTED.getValue(),
                                   VoteStatus.SOMEWHAT_TRUSTED.getValue(), VoteStatus.HIGHLY_DISTRUSTED.getValue())),
            () -> assertThat(response.getVoteRankings()).usingRecursiveComparison()
                .comparingOnlyFields("count")
                .isEqualTo(List.of(3, 2, 1, 0))
        );
    }

    @DisplayName("GET /issue/{issueId}/time-line 요청을 받아 상태 코드 200과 해당 이슈의 타임라인을 반환한다.")
    @Test
    void getIssueTimeLine() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "이미지링크", "캡션", "원본링크", Category.ECONOMY));
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
