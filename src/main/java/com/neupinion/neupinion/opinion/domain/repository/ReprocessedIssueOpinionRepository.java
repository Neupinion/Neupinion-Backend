package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueOpinionRepository extends JpaRepository<ReprocessedIssueOpinion, Long> {

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);
}
