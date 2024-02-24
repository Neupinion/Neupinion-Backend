package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.exception.ParagraphException.ParagraphNotFoundException;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpIssueParagraphRepository extends JpaRepository<FollowUpIssueParagraph, Long> {

    default FollowUpIssueParagraph getById(final Long id) {
        return findById(id)
            .orElseThrow(() -> new ParagraphNotFoundException(
                Map.of("paragraphId", id.toString()
                )
            ));
    }
}
