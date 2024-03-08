package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueTagRepository extends JpaRepository<ReprocessedIssueTag, Long> {

    List<ReprocessedIssueTag> findByReprocessedIssueId(final Long reprocessedIssueId);
}
