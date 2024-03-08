package com.neupinion.neupinion.issue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueParagraphRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;

    @Test
    void 재가공_이슈의_ID로_문단을_ID_오름차순으로_조회한다() {
        // given
        final long reprocessedIssueId = 1L;
        final ReprocessedIssueParagraph paragraph1 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph2 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph3 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph4 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));
        final ReprocessedIssueParagraph paragraph5 = reprocessedIssueParagraphRepository.save(
            ReprocessedIssueParagraph.forSave("내용", true, reprocessedIssueId));

        saveAndClearEntityManager();

        // when
        final List<ReprocessedIssueParagraph> paragraphs = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(
            reprocessedIssueId);

        // then
        assertThat(paragraphs).usingRecursiveComparison()
            .comparingOnlyFields("id")
            .isEqualTo(List.of(paragraph1, paragraph2, paragraph3, paragraph4, paragraph5));
    }
}
