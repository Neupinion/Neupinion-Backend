package com.neupinion.neupinion.opinion.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.opinion.application.dto.OpinionLikeRequest;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.utils.RestAssuredSpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class OpinionLikeControllerTest extends RestAssuredSpringBootTest {

    @Autowired
    private ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @DisplayName("PUT /reprocessed-issue/{id}/opinion/{opinionId}/like 요청을 받아 의견 좋아요를 업데이트하고, 200 OK 를 반환한다.")
    @Test
    void likeOpinion() {
        // given
        final long memberId = 1L;
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, reprocessedIssueId, true, memberId, "content"));

        // when
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new OpinionLikeRequest(true))
            .when().log().all()
            .put("/reprocessed-issue/{id}/opinion/{opinionId}/like", reprocessedIssueId, opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value());

        // then
        assertThat(reprocessedIssueOpinionLikeRepository.existsByMemberIdAndReprocessedIssueOpinionIdAndIsDeletedFalse(
            memberId, opinion.getId())).isTrue();
    }

    @DisplayName("PUT /reprocessed-issue/{id}/opinion/{opinionId}/like 요청을 받아 기존에 존재하던 의견 좋아요를 업데이트하고, 200 OK 를 반환한다.")
    @Test
    void likeExistedOpinion() {
        // given
        final long memberId = 1L;
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(1L, reprocessedIssueId, true, memberId, "content"));

        final ReprocessedIssueOpinionLike like = reprocessedIssueOpinionLikeRepository.save(
            ReprocessedIssueOpinionLike.forSave(memberId, opinion.getId()));

        // when
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new OpinionLikeRequest(false))
            .when().log().all()
            .put("/reprocessed-issue/{id}/opinion/{opinionId}/like", reprocessedIssueId, opinion.getId())
            .then().log().all()
            .statusCode(HttpStatus.OK.value());

        // then
        assertThat(reprocessedIssueOpinionLikeRepository.findById(like.getId()).get().getIsDeleted()).isTrue();
    }
}
