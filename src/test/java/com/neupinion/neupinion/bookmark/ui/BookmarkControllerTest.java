package com.neupinion.neupinion.bookmark.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.auth.application.TokenProvider;
import com.neupinion.neupinion.bookmark.application.dto.ReprocessedIssueBookmarkRequest;
import com.neupinion.neupinion.bookmark.domain.ReprocessedIssueBookmark;
import com.neupinion.neupinion.bookmark.domain.repository.ReprocessedIssueBookmarkRepository;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class BookmarkControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueBookmarkRepository reprocessedIssueBookmarkRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("PUT /reprocessed-issue/{id}/bookmark 요청을 받아 처음으로 북마크를 등록하고, 200 OK 를 반환한다.")
    @Test
    void register_first() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목1", "image", "이미지", Category.ECONOMY));
        final ReprocessedIssueBookmarkRequest request = new ReprocessedIssueBookmarkRequest(true);
        final long memberId = memberRepository.save(Member.forSave("이름", "image")).getId();

        // when
        RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .put("/reprocessed-issue/{id}/bookmark", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value());

        // then
        assertThat(reprocessedIssueBookmarkRepository.existsByReprocessedIssueIdAndMemberIdAndIsBookmarkedIsTrue(
            reprocessedIssue.getId(),
            memberId)).isTrue();
    }

    @DisplayName("PUT /reprocessed-issue/{id}/bookmark 요청을 받아 북마크를 해제하고, 200 OK 를 반환한다.")
    @Test
    void register_alreadyExisted() {
        // given
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("재가공 이슈 제목1", "image", "이미지", Category.ECONOMY));
        final long memberId = memberRepository.save(Member.forSave("이름", "image")).getId();
        reprocessedIssueBookmarkRepository.save(ReprocessedIssueBookmark.forSave(reprocessedIssue.getId(), memberId));
        final ReprocessedIssueBookmarkRequest request = new ReprocessedIssueBookmarkRequest(false);

        // when
        RestAssured.given().log().all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createAccessToken(memberId))
            .contentType(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(request)
            .when().log().all()
            .put("/reprocessed-issue/{id}/bookmark", reprocessedIssue.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value());

        // then
        assertThat(reprocessedIssueBookmarkRepository.existsByReprocessedIssueIdAndMemberIdAndIsBookmarkedIsTrue(
            reprocessedIssue.getId(), memberId))
            .isFalse();
    }
}
