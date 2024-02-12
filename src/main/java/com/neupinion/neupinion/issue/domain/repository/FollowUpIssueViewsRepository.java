package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.FollowUpIssueViews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpIssueViewsRepository extends JpaRepository<FollowUpIssueViews, Long> {

    boolean existsByFollowUpIssueIdAndMemberId(final Long followUpIssueId, final Long memberId);
}
