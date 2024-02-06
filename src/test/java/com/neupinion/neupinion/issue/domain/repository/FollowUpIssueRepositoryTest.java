package com.neupinion.neupinion.issue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.repository.dto.FollowUpIssueWithReprocessedIssueTitle;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowUpIssueRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private FollowUpIssueRepository followUpIssueRepository;

    @Test
    void 카테고리와_날짜로_후속_이슈를_조회한다() {
        // given
        final Category category = Category.ECONOMY;
        final Category otherCategory = Category.SOCIETY;
        final Clock clock = Clock.fixed(Instant.parse("2024-02-06T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final Clock otherClock = Clock.fixed(Instant.parse("2024-03-06T10:00:00Z"), ZoneId.of("Asia/Seoul"));

        final FollowUpIssue followUpIssue1 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목1", "https://neupinion.com/image.jpg", category, FollowUpIssueTag.INTERVIEW,
                                  1L, clock));
        final FollowUpIssue followUpIssue2 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목2", "https://neupinion.com/image.jpg", category, FollowUpIssueTag.INTERVIEW,
                                  2L, clock));
        final FollowUpIssue followUpIssue3 = followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목3", "https://neupinion.com/image.jpg", category, FollowUpIssueTag.INTERVIEW,
                                  3L, clock));
        followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목4", "https://neupinion.com/image.jpg", otherCategory,
                                  FollowUpIssueTag.INTERVIEW,
                                  4L, clock));
        followUpIssueRepository.save(
            FollowUpIssue.forSave("후속 이슈 제목5", "https://neupinion.com/image.jpg", otherCategory,
                                  FollowUpIssueTag.INTERVIEW,
                                  5L, otherClock));

        saveAndClearEntityManager();

        // when
        final List<FollowUpIssueWithReprocessedIssueTitle> result = followUpIssueRepository.findByCategoryAndDate(
            category, LocalDate.of(2024, 2, 6));

        // then
        assertAll(
            () -> assertThat(result).hasSize(3),
            () -> assertThat(result.stream()
                                 .map(FollowUpIssueWithReprocessedIssueTitle::getFollowUpIssue)
                                 .map(FollowUpIssue::getId)
                                 .toList()).containsExactlyInAnyOrder(followUpIssue1.getId(), followUpIssue2.getId(),
                                                                      followUpIssue3.getId())
        );
    }
}
