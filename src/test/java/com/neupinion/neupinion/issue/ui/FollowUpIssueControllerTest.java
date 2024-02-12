package com.neupinion.neupinion.issue.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.application.ReprocessedIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.Opinion;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueViewsRepository;
import com.neupinion.neupinion.issue.domain.repository.OpinionRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class FollowUpIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueService reprocessedIssueService;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private FollowUpIssueViewsRepository followUpIssueViewsRepository;

    @DisplayName("POST /follow-up-issue 요청을 보내는 경우, 상태 코드 201과 후속 이슈를 생성한다.")
    @Test
    void createFollowUpIssue() {
        // given
        final Long reprocessedIssueId = reprocessedIssueService.createReprocessedIssue(
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

    @DisplayName("GET /follow-up-issue/{category} 요청을 보내는 경우, 상태 코드 200과 해당 날짜, 카테고리에 맞는 후속 이슈들을 반환한다.")
    @Test
    void findFollowUpIssueByCategoryAndDate() {
        // given
        final Category category = Category.WORLD;
        final String dateFormat = "20240206";
        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T00:00:00Z"), ZoneId.systemDefault());

        final Long reprocessedIssueId = reprocessedIssueService.createReprocessedIssue(
            ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", category.name()));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));
        final FollowUpIssue followUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));

        opinionRepository.save(Opinion.forSave(reprocessedIssueId, 1L));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/follow-up-issue?category={category}&date={dateFormat}", category.name(), dateFormat)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().body()
            .jsonPath().getList(".", FollowUpIssueByCategoryResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses.stream()
                                 .map(FollowUpIssueByCategoryResponse::getId)
                                 .toList()).containsExactlyInAnyOrder(followUpIssue1.getId(), followUpIssue2.getId(),
                                                                      followUpIssue3.getId())
        );
    }

    @DisplayName("GET /follow-up-issue/{category}?viewMode=voted 요청을 보내는 경우, 상태 코드 200과 해당 날짜, 카테고리에 맞는 후속 이슈들을 반환한다.")
    @Test
    void findFollowUpIssueByCategoryAndDateAndVoted() {
        // given
        final Category category = Category.WORLD;
        final String dateFormat = "20240206";
        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T00:00:00Z"), ZoneId.systemDefault());

        final Long reprocessedIssueId = reprocessedIssueService.createReprocessedIssue(
            ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", category.name()));
        final Long otherReprocessedIssue = reprocessedIssueService.createReprocessedIssue(
            ReprocessedIssueCreateRequest.of("다른 재가공 이슈 제목", "image", category.name()));

        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));
        final FollowUpIssue followUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId, clock));
        followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목4", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, otherReprocessedIssue, clock));

        opinionRepository.save(Opinion.forSave(reprocessedIssueId, 1L));

        // when
        final var responses = RestAssured.given().log().all()
            .when().log().all()
            .get("/follow-up-issue?category={category}&date={dateFormat}&viewMode={voted}", category.name(), dateFormat,
                 "voted")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().body()
            .jsonPath().getList(".", FollowUpIssueByCategoryResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses.stream()
                                 .map(FollowUpIssueByCategoryResponse::getId)
                                 .toList()).containsExactlyInAnyOrder(followUpIssue1.getId(), followUpIssue2.getId(),
                                                                      followUpIssue3.getId())
        );
    }

    @DisplayName("GET /follow-up-issue/{id} 요청을 보내는 경우, 후속 이슈를 조회하고, 유저의 조회 기록을 저장한다.")
    @Test
    void findById() {
        // given
        final Category category = Category.WORLD;
        final Long reprocessedIssueId = reprocessedIssueService.createReprocessedIssue(
            ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", category.name()));
        final FollowUpIssue followUpIssue = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category,
                                  FollowUpIssueTag.OFFICIAL_POSITION, reprocessedIssueId));
        final Long memberId = 1L;

        // when
        final var response = RestAssured.given().log().all()
            .when().log().all()
            .get("/follow-up-issue/{id}", followUpIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().body()
            .as(FollowUpIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(followUpIssueViewsRepository.existsByFollowUpIssueIdAndMemberId(followUpIssue.getId(),
                                                                                             memberId)).isTrue(),
            () -> assertThat(followUpIssue).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(response)
        );
    }
}
