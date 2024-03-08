package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueBookmarkRepository extends JpaRepository<ReprocessedIssueBookmark, Long> {

    boolean existsByReprocessedIssueIdAndMemberId(final Long id, final Long memberId);
}
