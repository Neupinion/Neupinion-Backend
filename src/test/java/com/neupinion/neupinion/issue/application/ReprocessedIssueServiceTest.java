package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@Sql(value = "classpath:schema.sql")
@SpringBootTest
class ReprocessedIssueServiceTest {

    @Autowired
    private ReprocessedIssueRepository reprocessedIssueRepository;

    private ReprocessedIssueService reprocessedIssueService;

    @BeforeEach
    void setUp() {
        reprocessedIssueService = new ReprocessedIssueService(reprocessedIssueRepository);
    }

    @Test
    void 특정_날짜의_재가공_이슈를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final Clock clock2 = Clock.fixed(Instant.parse("2024-02-05T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", Category.ECONOMY, clock2));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목4", "image", Category.ECONOMY, clock));
        final ReprocessedIssue issue5 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목5", "image", Category.ECONOMY, clock));

        // when
        List<ReprocessedIssueResponse> response = reprocessedIssueService.findReprocessedIssues("20240204");

        // then
        // 5 4 3 2
        assertThat(response.stream()
                       .map(ReprocessedIssueResponse::getId)
                       .toList())
            .containsExactlyInAnyOrder(issue5.getId(), issue4.getId(), issue3.getId(), issue2.getId());
    }
}
