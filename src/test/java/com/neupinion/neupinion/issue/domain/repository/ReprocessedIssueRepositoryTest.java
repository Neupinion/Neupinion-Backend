package com.neupinion.neupinion.issue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Test
    void 재가공_이슈_정보와_댓글_개수를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));

        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", Category.ECONOMY, clock));
        System.out.println("time" + LocalDateTime.now(clock));

        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue1.getId(), 1L, "댓글1", 1L));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue1.getId(), 2L, "댓글2", 2L));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue1.getId(), 3L, "댓글3", 3L));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue2.getId(), 1L, "댓글1", 1L));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue2.getId(), 2L, "댓글2", 2L));
        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(issue3.getId(), 1L, "댓글1", 3L));

        saveAndClearEntityManager();

        // when
        final var response = reprocessedIssueRepository.findByCreatedAt(LocalDate.of(2024, 2, 4),
                                                                        PageRequest.of(0, 4));
        // then
        assertAll(
            () -> assertThat(response).hasSize(3),
            () -> assertThat(response.stream()
                                 .map(ReprocessedIssueWithCommentCount::getReprocessedIssue)
                                 .map(ReprocessedIssue::getId)
                                 .toList()).containsExactlyInAnyOrder(issue1.getId(), issue2.getId(), issue3.getId()),
            () -> assertThat(response.stream()
                                 .map(ReprocessedIssueWithCommentCount::getCommentCount)
                                 .toList()).containsExactlyInAnyOrder(3L, 2L, 1L)
        );
    }
}
