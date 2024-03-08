package com.neupinion.neupinion.issue.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
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

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Autowired
    private ReprocessedIssueTagRepository reprocessedIssueTagRepository;

    private ReprocessedIssueService reprocessedIssueService;

    @BeforeEach
    void setUp() {
        reprocessedIssueService = new ReprocessedIssueService(reprocessedIssueRepository,
                                                              reprocessedIssueParagraphRepository,
                                                              reprocessedIssueTagRepository);
    }

    @Test
    void 특정_날짜의_재가공_이슈를_조회한다() {
        // given
        final Clock clock = Clock.fixed(Instant.parse("2024-02-04T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final Clock clock2 = Clock.fixed(Instant.parse("2024-02-05T10:00:00Z"), ZoneId.of("Asia/Seoul"));
        final ReprocessedIssue issue1 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock2));
        final ReprocessedIssue issue2 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목2", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue3 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목3", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue4 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목4", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));
        final ReprocessedIssue issue5 = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목5", "image", "이미지 캡션", "originUrl", Category.ECONOMY, clock));

        // when
        final List<ShortReprocessedIssueResponse> issues = reprocessedIssueService.findReprocessedIssues("20240204");

        // then
        // 5 4 3 2
        assertThat(issues.stream()
                       .map(ShortReprocessedIssueResponse::getId)
                       .toList())
            .containsExactlyInAnyOrder(issue5.getId(), issue4.getId(), issue3.getId(), issue2.getId());
    }

    @Test
    void 재가공_이슈의_내용을_조회한다() {
        // given
        final ReprocessedIssue issue = reprocessedIssueRepository.save(
            ReprocessedIssue.forSave("제목1", "image", "이미지 캡션", "originUrl", Category.ECONOMY));
        final ReprocessedIssueParagraph paragraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용1", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용2", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용3", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph4 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용4", true, issue.getId()));
        final ReprocessedIssueParagraph paragraph5 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용5", true, issue.getId()));

        final ReprocessedIssueTag tag1 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그1"));
        final ReprocessedIssueTag tag2 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그2"));
        final ReprocessedIssueTag tag3 = reprocessedIssueTagRepository.save(
            ReprocessedIssueTag.forSave(issue.getId(), "태그3"));

        // when
        final ReprocessedIssueResponse response = reprocessedIssueService.findReprocessedIssue(issue.getId());

        // then
        assertThat(response.getContent())
            .usingRecursiveComparison()
            .comparingOnlyFields("id")
            .isEqualTo(List.of(paragraph1, paragraph2, paragraph3, paragraph4, paragraph5));
        assertThat(response.getTags())
            .containsExactlyInAnyOrder(tag1.getTag(), tag2.getTag(), tag3.getTag());
    }
}
