package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.opinion.domain.repository.dto.IssueCommentMapping;
import java.time.LocalDate;
import java.util.Date;
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

    @Query(value = "SELECT ri "
        + "FROM ReprocessedIssue ri "
        + "WHERE ri.category = :category "
        + "AND ri.id != :currentReprocessedIssueId "
        + "ORDER BY ri.createdAt DESC "
        + "LIMIT 3"
    )
    List<ReprocessedIssue> findCurrentReprocessedIssuesByCategory(final Category category,
                                                                  final Long currentReprocessedIssueId);

    @Query(value = "SELECT ri "
        + "FROM ReprocessedIssue ri "
        + "WHERE ri.id <> :id "
        + "AND CAST(ri.createdAt AS DATE) >= :standard "
        + "ORDER BY FUNCTION('RAND') "
    )
    List<ReprocessedIssue> findRandomReprocessedIssuesExceptId(final Long id, final Date standard,
                                                               final Pageable pageable);

    @Query(name = "ReprocessedIssue.findAllCommentsOrderByCreatedAtDesc", nativeQuery = true)
    List<IssueCommentMapping> findAllCommentsOrderByCreatedAtDesc(final Long issueId,
                                                                  final List<Long> followUpIssueIds,
                                                                  final Pageable pageable);
}
