package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.FollowUpIssueTrustVote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpIssueTrustVoteRepository extends JpaRepository<FollowUpIssueTrustVote, Long>{

    List<FollowUpIssueTrustVote> findByFollowUpIssueIdIn(final List<Long> followUpIssueIds);
}
