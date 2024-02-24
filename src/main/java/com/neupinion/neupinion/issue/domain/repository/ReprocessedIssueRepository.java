package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReprocessedIssueRepository extends JpaRepository<ReprocessedIssue, Long> {

    @Query(value = "SELECT ri AS reprocessedIssue, COUNT(ic.id) AS commentCount "
        + "FROM ReprocessedIssue ri "
        + "LEFT JOIN ReprocessedIssueOpinion ic ON ri.id = ic.reprocessedIssueId "
        + "WHERE CAST(ri.createdAt AS DATE) = :createdAt "
        + "GROUP BY ri.id",
        countQuery = "SELECT COUNT(ri) "
            + "FROM ReprocessedIssue ri "
            + "LEFT JOIN ReprocessedIssueOpinion ic ON ri.id = ic.reprocessedIssueId "
            + "WHERE CAST(ri.createdAt AS DATE) = :createdAt"
    )
    List<ReprocessedIssueWithCommentCount> findByCreatedAt(final LocalDate createdAt, final Pageable pageable);
}
