package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.exception.ParagraphException.ParagraphNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueParagraphRepository extends JpaRepository<ReprocessedIssueParagraph, Long> {

    default ReprocessedIssueParagraph getById(final Long id) {
        return findById(id)
            .orElseThrow(() -> new ParagraphNotFoundException(
                Map.of("paragraphId", id.toString()
                )
            ));
    }

    List<ReprocessedIssueParagraph> findByReprocessedIssueIdOrderById(final Long reprocessedIssueId);

    List<ReprocessedIssueParagraph> findByReprocessedIssueIdAndSelectableTrueOrderById(final Long reprocessedIssueId);
}
