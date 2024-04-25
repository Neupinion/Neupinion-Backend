package com.neupinion.neupinion.issue.ui;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.neupinion.neupinion.issue.application.dto.RelatedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class RelatedIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @DisplayName("GET /reprocessed-issue/{id}/related-issue 요청을 받아 200 OK와 관련된 이슈들을 반환한다.")
    @Test
    void getRelatedIssues() {
        // given
        final Random random = mock(Random.class);
        when(random.nextInt(anyInt()))
            .thenReturn(0);
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("title", "image", "caption", "url", Category.SOCIETY));
        final ReprocessedIssue reprocessedIssue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("title", "image", "caption", "url", Category.SOCIETY));
        final FollowUpIssue followUpIssue = followUpIssueRepository.save(
            FollowUpIssue.forSave("title", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("title", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue2.getId()));
        final FollowUpIssue followUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("title", "image", Category.POLITICS, FollowUpIssueTag.TRIAL_RESULTS,
                                  reprocessedIssue2.getId()));

        // when
        var responses = given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/" + reprocessedIssue.getId() + "/related-issue")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath().getList(".", RelatedIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).extracting("id")
                .containsExactlyInAnyOrder(reprocessedIssue2.getId(), followUpIssue2.getId(), followUpIssue3.getId())
        );
    }
}
