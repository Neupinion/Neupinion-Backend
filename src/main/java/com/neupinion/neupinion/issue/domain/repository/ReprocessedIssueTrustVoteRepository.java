package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueTrustVoteRepository extends JpaRepository<ReprocessedIssueTrustVote, Long> {

    Optional<ReprocessedIssueTrustVote> findByReprocessedIssueIdAndMemberId(final Long id, final Long memberId);
}
