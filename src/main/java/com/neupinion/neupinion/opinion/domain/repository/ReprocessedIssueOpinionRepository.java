package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.exception.OpinionException.NotFoundOpinionException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueOpinionRepository extends JpaRepository<ReprocessedIssueOpinion, Long> {

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);

    default ReprocessedIssueOpinion getById(final Long id) {
        return findById(id)
            .orElseThrow(NotFoundOpinionException::new);
    }

    List<ReprocessedIssueOpinion> findByMemberIdAndReprocessedIssueId(final Long memberId,
                                                                      final Long reprocessedIssueId);
}
