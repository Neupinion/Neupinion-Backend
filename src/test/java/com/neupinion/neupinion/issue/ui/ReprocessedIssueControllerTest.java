package com.neupinion.neupinion.issue.ui;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.auth.application.TokenProvider;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssuesByReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.RecentReprocessedIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueParagraphRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.TrustVoteRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.RelatableStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class ReprocessedIssueControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueTagRepository reprocessedIssueTagRepository;

    @Autowired
    private ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Autowired
    private IssueStandRepository issueStandRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("GET /reprocessed-issue?date={date} 로 요청을 보내는 경우, 상태 코드 200과 해당 날짜의 재가공 이슈 리스트를 반환한다.")
    @Test
    void findReprocessedIssues() {
        // given
        final String date = "20240206";
        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T00:00:00Z"), ZoneId.systemDefault());
        final Clock otherClock = Clock.fixed(Instant.parse("2024-03-06T00:00:00Z"), ZoneId.systemDefault());

        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목1", "image", "이미지", Category.ECONOMY, clock));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목2", "image", "이미지", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목3", "image", "이미지", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목4", "image", "이미지", Category.SOCIETY, clock));
        reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목5", "image", "이미지", Category.ECONOMY, otherClock));

        // when
        final var response = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue?date={date}", date)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList(".", ShortReprocessedIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(response).hasSize(4),
            () -> assertThat(response).extracting("id").containsExactlyInAnyOrder(
                issue1.getId(), issue2.getId(), issue3.getId(), issue4.getId()
            )
        );
    }

    @DisplayName("POST /reprocessed-issue 로 요청을 보내는 경우, 상태 코드 201과 해당 날짜의 재가공 이슈를 생성한다.")
    @Test
    void saveReprocessedIssue() {
        // given
        final var request = ReprocessedIssueCreateRequest.of("재가공 이슈 제목", "image", "이미지", List.of("url1", "url2"),
                                                             List.of("url1", "url2"), "ECONOMY",
                                                                List.of(new ReprocessedIssueParagraphRequest("내용1", true),
                                                                        new ReprocessedIssueParagraphRequest("내용2", true)),
                                                             List.of("찬성", "반대"));

        // when
        final var response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .post("/reprocessed-issue")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header("Location");

        // then
        assertThat(reprocessedIssueRepository.existsById(valueOf(response.substring(response.lastIndexOf("/") + 1))))
            .isTrue();
    }

    @DisplayName("GET /reprocessed-issue/{id} 로 요청을 보내는 경우, 상태 코드 200과 해당 id의 재가공 이슈를 반환한다.")
    @Test
    void findReprocessedIssueResponses() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "image", "이미지", Category.ECONOMY));
        issueStandRepository.saveAll(List.of(IssueStand.forSave("찬성", reprocessedIssue.getId()),
                                             IssueStand.forSave("반대", reprocessedIssue.getId())));

        final ReprocessedIssueParagraph paragraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용1", true, reprocessedIssue.getId()));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용2", true, reprocessedIssue.getId()));
        final ReprocessedIssueTag tag1 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(reprocessedIssue.getId(), "태그1"));
        final ReprocessedIssueTag tag2 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(reprocessedIssue.getId(), "태그2"));

        final Member member = memberRepository.save(Member.forSave("닉네임", "image"));

        // when
        final var response = RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(member.getId()))
            .contentType(ContentType.JSON)
            .when().log().all()
            .get("/reprocessed-issue/{id}", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ReprocessedIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getId()).isEqualTo(reprocessedIssue.getId()),
            () -> assertThat(response.getTitle()).isEqualTo(reprocessedIssue.getTitle()),
            () -> assertThat(response.getImageUrl()).isEqualTo(reprocessedIssue.getImageUrl()),
            () -> assertThat(response.getCategory()).isEqualTo(reprocessedIssue.getCategory().getValue()),
            () -> assertThat(response.getContent()).hasSize(2),
            () -> assertThat(response.getContent()).extracting("id")
                .containsExactlyInAnyOrder(paragraph1.getId(), paragraph2.getId()),
            () -> assertThat(response.getTags()).containsExactlyInAnyOrder(tag1.getTag(), tag2.getTag())
        );
    }

    @DisplayName("PUT /reprocessed-issue/{id}/trust-vote 로 요청을 보내는 경우, 상태 코드 200을 리턴하고 해당 id의 재가공 이슈에 투표한다.")
    @Test
    void voteTrust() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "image", "이미지", Category.ECONOMY));
        final List<IssueStand> issueStands = issueStandRepository.saveAll(List.of(
            IssueStand.forSave("찬성", reprocessedIssue.getId()),
            IssueStand.forSave("반대", reprocessedIssue.getId())
        ));

        final TrustVoteRequest request = new TrustVoteRequest(issueStands.get(0).getId(), true,
                                                              issueStands.get(1).getId(), false);
        final long memberId = memberRepository.save(Member.forSave("닉네임", "image")).getId();

        // when
        // then
        RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .put("/reprocessed-issue/{id}/trust-vote", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("GET /reprocessed-issue?current={id}&category={category} 로 요청을 보내는 경우, 상태 코드 200과 해당 카테고리의 재가공 이슈 리스트를 반환한다.")
    @Test
    void findReprocessedIssueResponsesByCategory() {
        // given
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목1", "image", "이미지", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T00:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목2", "image", "이미지", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T06:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목3", "image", "이미지", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T08:00:00Z"), ZoneId.systemDefault())));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목4", "image", "이미지", Category.ECONOMY,
                                     Clock.fixed(Instant.parse("2024-03-18T10:00:00Z"), ZoneId.systemDefault())));
        final long memberId = memberRepository.save(Member.forSave("닉네임", "image")).getId();

        // when
        final var responses = RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .when().log().all()
            .get("/reprocessed-issue/by-category?current={id}&category={category}", issue1.getId(),
                 Category.ECONOMY.name())
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList(".", RecentReprocessedIssueByCategoryResponse.class);

        // then
        assertAll(
            () -> assertThat(responses).hasSize(3),
            () -> assertThat(responses).extracting("id")
                .containsExactlyInAnyOrder(issue4.getId(), issue3.getId(), issue2.getId())
        );
    }

    @DisplayName("GET /reprocessed-issue/{id}/trust-vote 로 요청을 보내는 경우, 상태 코드 200과 해당 id의 재가공 이슈 투표 결과를 반환한다.")
    @Test
    void getVoteResult() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "image", "이미지", Category.ECONOMY));
        final List<IssueStand> issueStands = issueStandRepository.saveAll(
            List.of(IssueStand.forSave("찬성", reprocessedIssue.getId()),
                    IssueStand.forSave("반대", reprocessedIssue.getId())));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 1L,
                                              new RelatableStand(issueStands.get(0).getId(), true,
                                                                 issueStands.get(1).getId(), false)));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 2L,
                                              new RelatableStand(issueStands.get(0).getId(), true,
                                                                 issueStands.get(1).getId(), false)));
        reprocessedIssueTrustVoteRepository.save(
            ReprocessedIssueTrustVote.forSave(reprocessedIssue.getId(), 3L,
                                              new RelatableStand(issueStands.get(1).getId(), false,
                                                                 issueStands.get(0).getId(), true)));

        // when
        final var response = RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(1L))
            .contentType(ContentType.JSON)
            .when().log().all()
            .get("/reprocessed-issue/{id}/trust-vote", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(ReprocessedIssueVoteResultResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getMostVotedCount()).isEqualTo(2),
            () -> assertThat(response.getTotalVoteCount()).isEqualTo(3),
            () -> assertThat(response.getMostVotedStand()).isEqualTo("찬성"),
            () -> assertThat(response.getVoteRankings()).hasSize(2),
            () -> assertThat(response.getVoteRankings().get(0).getStand()).isEqualTo("찬성"),
            () -> assertThat(response.getVoteRankings().get(1).getStand()).isEqualTo("반대"),
            () -> assertThat(response.getVoteRankings().get(0).getRelatablePercentage()).isEqualTo(66),
            () -> assertThat(response.getVoteRankings().get(1).getRelatablePercentage()).isEqualTo(33)
        );
    }

    @DisplayName("GET /reprocessed-issue/{id}/follow-up-issue 로 요청을 보내는 경우, 상태 코드 200과 해당 id의 재가공 이슈의 후속 이슈 3개를 반환한다.")
    @Test
    void getFollowUpIssues() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목", "image", "이미지", Category.ECONOMY));
        final FollowUpIssue followUpIssue = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "image", Category.ECONOMY, FollowUpIssueTag.INTERVIEW,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "image", Category.ECONOMY, FollowUpIssueTag.INTERVIEW,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "image", Category.ECONOMY, FollowUpIssueTag.INTERVIEW,
                                  reprocessedIssue.getId()));
        final FollowUpIssue followUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목4", "image", Category.ECONOMY, FollowUpIssueTag.INTERVIEW,
                                  reprocessedIssue.getId()));

        // when
        final var response = RestAssured.given().log().all()
            .when().log().all()
            .get("/reprocessed-issue/{id}/follow-up-issue", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(FollowUpIssuesByReprocessedIssueResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getFollowUpIssues()).hasSize(3),
            () -> assertThat(response.getFollowUpIssues()).extracting("id")
                .containsExactlyInAnyOrder(followUpIssue1.getId(), followUpIssue2.getId(), followUpIssue3.getId()),
            () -> assertThat(response.getIsIntegratedVotePossible()).isTrue()
        );
    }
}
